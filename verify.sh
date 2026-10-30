#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
./gradlew --dependency-verification strict --console=plain testDebugUnitTest lintRelease assembleRelease
