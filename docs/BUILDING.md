# Сборка

## Инструменты

| Компонент | Версия |
| --- | --- |
| JDK | 17 |
| Android Gradle Plugin | 8.9.2 |
| Gradle Wrapper | 8.13 |
| compileSdk / targetSdk | 35 / 35 |
| Build Tools | 35.0.0 |
| minSdk | 26 — Android 8.0 |

NDK и CMake не нужны. Сборка использует обычный Android SDK, без нативных модулей.

## Android Studio

1. Откройте корень репозитория — каталог с `settings.gradle`.
2. В SDK Manager установите Android SDK Platform 35 и Build Tools 35.0.0.
3. В Gradle JDK выберите JDK 17.
4. Дождитесь Gradle Sync. Для запуска выберите конфигурацию приложения `app`.

Android Studio создаст `local.properties`. Этот файл не нужно коммитить.

## Командная строка

Укажите `JAVA_HOME` на JDK 17, а `ANDROID_HOME` — на каталог Android SDK.
Вместо `ANDROID_HOME` можно создать `local.properties`:

```properties
sdk.dir=/absolute/path/to/android-sdk
```

Linux/macOS:

```sh
python3 scripts/check_repo.py
./verify.sh
./gradlew --dependency-verification strict assembleDebug
```

Windows, PowerShell или cmd:

```bat
python scripts/check_repo.py
gradlew.bat --dependency-verification strict testDebugUnitTest lintRelease assembleRelease
gradlew.bat --dependency-verification strict assembleDebug
```

Если после распаковки ZIP потерялись права исполнения, на Linux/macOS выполните
`chmod +x gradlew verify.sh build.sh`. В Git права должны сохраниться.

Первая сборка требует интернет. Debug APK находится в
`app/build/outputs/apk/debug/app-debug.apk`, неподписанный release — в
`app/build/outputs/apk/release/app-release-unsigned.apk`.

## Release и подпись

В release включены R8 и удаление неиспользуемых ресурсов. Словарь содержит
52 имени: `a–z`, `A–Z`. Сам по себе словарь не гарантирует уменьшение размера:
R8 уже генерирует короткие имена и расширяет набор при необходимости.

Для устанавливаемого release используйте Android Studio:
**Build → Generate Signed App Bundle or APK → APK**. Ключ храните вне Git.
Сведения о нём и пароли не должны попадать в `build.gradle` и GitHub Issues.

CLI: выполните zipalign и apksigner из Android SDK Build Tools. Пример для
PKCS12, у которого пароль ключа совпадает с паролем хранилища:

```sh
zipalign -p -f 4 app-release-unsigned.apk aligned.apk
apksigner sign --ks /private/calendar-release.p12 --ks-type PKCS12 \
  --ks-key-alias calendar --ks-pass file:/private/password.txt \
  --v4-signing-enabled false --out calendar-release.apk aligned.apk
apksigner verify --verbose --print-certs calendar-release.apk
```

Для обновлений нужны тот же `applicationId`, тот же ключ подписи
и увеличенный `versionCode`. APK из CI не подписывается приватным release-ключом:
debug — только для тестирования, unsigned release — для последующей подписи.

У поставляемого APK 1.2 сертификат SHA-256:

```text
f8b2e14537e67ed657244ae780b023675208fc89d08f627fdfdd13d6f190f675
```

У версии 1.2 новая подпись относительно 1.1. Поверх 1.1 она не установится.
APK, собранный и подписанный вашим другим ключом, также не обновит поставляемый APK.

## Проверка зависимостей

Не отключайте strict verification и не обновляйте checksums, лишь бы убрать ошибку.
Сначала проверьте источник изменения. При намеренном обновлении библиотек нужно
проверить новые артефакты, обновить verification metadata и lockfile, затем
повторить тесты, Lint и release-сборку. Изменения checksums проверяйте в diff.

AndroidX: ViewPager2 1.1.0, RecyclerView 1.4.0, Core 1.16.0.
Core 1.16.0 оставлен совместимым с compileSdk 35; проверенный Core 1.19.1 требует
compileSdk 37 и AGP 9.1.0. Автоматически менять весь toolchain из-за уведомления
о новой версии библиотеки не следует.

## GitHub Actions

Workflow `Android` запускается на push, pull request и вручную. Он проверяет
чистоту репозитория и Wrapper, ставит JDK/SDK, выполняет unit-тесты, Android Lint,
release-сборку и debug-сборку. Отчёты и APK доступны во вкладке Actions, 7 дней.
UI-тесты с эмулятором в этот workflow не включены.
