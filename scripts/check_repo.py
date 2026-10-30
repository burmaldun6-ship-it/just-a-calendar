#!/usr/bin/env python3
"""Small repository hygiene check; not a general-purpose secret scanner."""
from pathlib import Path
import hashlib
import re
import subprocess
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
WRAPPER_SHA256 = '81a82aaea5abcc8ff68b3dfcb58b3c3c429378efd98e7433460610fecd7ae45f'
DISTRIBUTION_SHA256 = '20f1b1176237254a6fc204d8434196fa11a4cfb387567519c61556e8710aed78'
errors = []


def require(condition, message):
    if not condition:
        errors.append(message)


def repository_files():
    # A Git index reveals already tracked secrets even if .gitignore hides them.
    try:
        result = subprocess.run(
            ['git', '-C', str(ROOT), 'ls-files', '--cached', '-z'],
            check=True, capture_output=True,
        )
        names = [n for n in result.stdout.decode('utf-8').split('\0') if n]
        if names:
            return [ROOT / n for n in names]
    except (FileNotFoundError, subprocess.CalledProcessError):
        pass
    excluded = {'.git', '.gradle', '.idea', '.vscode', 'build', '__pycache__', 'ui-test-output'}
    return [p for p in ROOT.rglob('*') if p.is_file() and not excluded.intersection(p.relative_to(ROOT).parts)]


wrapper = ROOT / 'gradle/wrapper/gradle-wrapper.jar'
require(wrapper.is_file(), 'Gradle Wrapper JAR is missing')
if wrapper.is_file():
    require(hashlib.sha256(wrapper.read_bytes()).hexdigest() == WRAPPER_SHA256,
            'Gradle Wrapper JAR checksum mismatch; investigate before changing the pin')

properties_path = ROOT / 'gradle/wrapper/gradle-wrapper.properties'
properties = properties_path.read_text(encoding='utf-8') if properties_path.exists() else ''
require('distributionSha256Sum=' + DISTRIBUTION_SHA256 in properties, 'Gradle distribution checksum is missing or changed')
require('gradle-8.13-bin.zip' in properties, 'Review Wrapper version and checksum pins together')

try:
    ns = {'v': 'https://schema.gradle.org/dependency-verification'}
    metadata = ET.parse(ROOT / 'gradle/verification-metadata.xml').getroot()
    require(metadata.findtext('v:configuration/v:verify-metadata', namespaces=ns) == 'true',
            'Dependency metadata verification must stay enabled')
    components = metadata.findall('v:components/v:component', ns)
    require(bool(components), 'Dependency verification has no components')
    for component in components:
        for artifact in component.findall('v:artifact', ns):
            checksums = artifact.findall('v:sha256', ns)
            require(bool(checksums) and all(re.fullmatch(r'[0-9a-f]{64}', c.get('value', '')) for c in checksums),
                    'Missing or invalid SHA-256 for ' + artifact.get('name', '?'))
except (OSError, ET.ParseError) as exc:
    errors.append('Cannot read verification metadata: ' + str(exc))

lockfile = ROOT / 'app/gradle.lockfile'
require(lockfile.is_file() and 'androidx.viewpager2:viewpager2:1.1.0=' in lockfile.read_text(encoding='utf-8'),
        'Application dependency lockfile is missing or unexpectedly changed')
dictionary = ROOT / 'app/obfuscation-dictionary.txt'
require(dictionary.is_file() and dictionary.read_text(encoding='utf-8').splitlines() == list('abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'),
        'R8 dictionary must contain a-z and A-Z, one name per line')

private_names = {'local.properties', 'keystore.properties', 'key.properties', 'secrets.properties', 'password.txt', 'passwords.txt', '.env'}
private_suffixes = {'.jks', '.keystore', '.p12', '.pfx', '.pem', '.key', '.apk', '.aab', '.apks', '.idsig'}
secret_markers = [
    re.compile(rb'-----BEGIN (?:[A-Z]+ )?PRIVATE KEY-----'),
    re.compile(rb'\bgh[pousr]_[A-Za-z0-9]{36,}\b'),
    re.compile(rb'\bgithub_pat_[A-Za-z0-9_]{40,}\b'),
]
files = repository_files()
for path in files:
    rel = path.relative_to(ROOT)
    lower = path.name.lower()
    require(not (lower in private_names or lower.startswith(('.env.', 'private-key-backup'))
                 or path.suffix.lower() in private_suffixes or 'signing' in rel.parts),
            'Private or generated file in repository: ' + str(rel))
    require(path.is_file() and not path.is_symlink(), 'Missing file or unexpected symlink: ' + str(rel))
    if path.is_file() and path.suffix.lower() not in {'.jar', '.png', '.jpg', '.jpeg', '.gif', '.webp'}:
        data = path.read_bytes()
        require(not any(marker.search(data) for marker in secret_markers),
                'Possible credential/private-key material in ' + str(rel) + ' (value not printed)')

for name in ['gradlew', 'verify.sh', 'build.sh']:
    path = ROOT / name
    require(path.exists() and b'\r' not in path.read_bytes(), 'Shell script must exist and use LF: ' + name)

if errors:
    for message in errors:
        print('ERROR: ' + message, file=sys.stderr)
    sys.exit(1)
print(f'OK: {len(files)} files checked; Wrapper/distribution pins, dependency metadata, lockfile and R8 dictionary validated.')
print('This is a limited current-tree/index check, not a full secret or Git history scan.')
