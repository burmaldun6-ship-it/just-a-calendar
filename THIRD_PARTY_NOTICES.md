# Сторонние компоненты

Авторский код календаря распространяется под [MIT](LICENSE).
Сторонние компоненты сохраняют собственные лицензии, перечисленные ниже.

## Gradle Wrapper

Gradle Wrapper 8.13 — Apache License 2.0. Текст лицензии включён в JAR
(`META-INF/LICENSE`) и сохранён отдельно: [Apache-2.0.txt](docs/licenses/Apache-2.0.txt).
Исходники: https://github.com/gradle/gradle/tree/v8.13.0/platforms/core-runtime/wrapper-main

## Зависимости приложения

Ниже — зависимости из `releaseRuntimeClasspath` зафиксированного lockfile.
Это полный список разрешённых зависимостей этой конфигурации, а не утверждение,
что R8 оставил каждый из этих модулей в APK. Названия лицензий взяты из POM
соответствующих версий из официальных Maven-репозиториев. POM, присутствующие
в verification metadata, дополнительно сверены по сохранённым SHA-256.
Версии и ссылки сохраняются для проверки происхождения компонентов.

Текст Apache License 2.0: [копия в репозитории](docs/licenses/Apache-2.0.txt).
AndroidX — Android Open Source Project и его участники; Kotlin, coroutines
и JetBrains annotations — JetBrains и участники; Guava и JSpecify — их авторы
и участники. Права на сторонний код остаются у соответствующих правообладателей.

| Компонент | Лицензия по POM | Источник |
| --- | --- | --- |
| `androidx.activity:activity:1.0.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/activity/activity/1.0.0/activity-1.0.0.pom) |
| `androidx.annotation:annotation-experimental:1.4.1` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/annotation/annotation-experimental/1.4.1/annotation-experimental-1.4.1.pom) |
| `androidx.annotation:annotation-jvm:1.8.1` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/annotation/annotation-jvm/1.8.1/annotation-jvm-1.8.1.pom) |
| `androidx.annotation:annotation:1.8.1` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/annotation/annotation/1.8.1/annotation-1.8.1.pom) |
| `androidx.arch.core:core-common:2.2.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/arch/core/core-common/2.2.0/core-common-2.2.0.pom) |
| `androidx.arch.core:core-runtime:2.2.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/arch/core/core-runtime/2.2.0/core-runtime-2.2.0.pom) |
| `androidx.collection:collection-jvm:1.4.2` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/collection/collection-jvm/1.4.2/collection-jvm-1.4.2.pom) |
| `androidx.collection:collection:1.4.2` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/collection/collection/1.4.2/collection-1.4.2.pom) |
| `androidx.concurrent:concurrent-futures:1.1.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/concurrent/concurrent-futures/1.1.0/concurrent-futures-1.1.0.pom) |
| `androidx.core:core-ktx:1.16.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/core/core-ktx/1.16.0/core-ktx-1.16.0.pom) |
| `androidx.core:core-viewtree:1.0.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/core/core-viewtree/1.0.0/core-viewtree-1.0.0.pom) |
| `androidx.core:core:1.16.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/core/core/1.16.0/core-1.16.0.pom) |
| `androidx.customview:customview-poolingcontainer:1.0.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/customview/customview-poolingcontainer/1.0.0/customview-poolingcontainer-1.0.0.pom) |
| `androidx.customview:customview:1.0.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/customview/customview/1.0.0/customview-1.0.0.pom) |
| `androidx.fragment:fragment:1.1.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/fragment/fragment/1.1.0/fragment-1.1.0.pom) |
| `androidx.interpolator:interpolator:1.0.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/interpolator/interpolator/1.0.0/interpolator-1.0.0.pom) |
| `androidx.lifecycle:lifecycle-common:2.6.2` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/lifecycle/lifecycle-common/2.6.2/lifecycle-common-2.6.2.pom) |
| `androidx.lifecycle:lifecycle-livedata-core:2.6.2` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/lifecycle/lifecycle-livedata-core/2.6.2/lifecycle-livedata-core-2.6.2.pom) |
| `androidx.lifecycle:lifecycle-livedata:2.6.2` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/lifecycle/lifecycle-livedata/2.6.2/lifecycle-livedata-2.6.2.pom) |
| `androidx.lifecycle:lifecycle-runtime:2.6.2` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/lifecycle/lifecycle-runtime/2.6.2/lifecycle-runtime-2.6.2.pom) |
| `androidx.lifecycle:lifecycle-viewmodel:2.6.2` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/lifecycle/lifecycle-viewmodel/2.6.2/lifecycle-viewmodel-2.6.2.pom) |
| `androidx.loader:loader:1.0.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/loader/loader/1.0.0/loader-1.0.0.pom) |
| `androidx.profileinstaller:profileinstaller:1.3.1` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/profileinstaller/profileinstaller/1.3.1/profileinstaller-1.3.1.pom) |
| `androidx.recyclerview:recyclerview:1.4.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/recyclerview/recyclerview/1.4.0/recyclerview-1.4.0.pom) |
| `androidx.savedstate:savedstate:1.0.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/savedstate/savedstate/1.0.0/savedstate-1.0.0.pom) |
| `androidx.startup:startup-runtime:1.1.1` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/startup/startup-runtime/1.1.1/startup-runtime-1.1.1.pom) |
| `androidx.tracing:tracing:1.2.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/tracing/tracing/1.2.0/tracing-1.2.0.pom) |
| `androidx.versionedparcelable:versionedparcelable:1.1.1` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/versionedparcelable/versionedparcelable/1.1.1/versionedparcelable-1.1.1.pom) |
| `androidx.viewpager2:viewpager2:1.1.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/viewpager2/viewpager2/1.1.0/viewpager2-1.1.0.pom) |
| `androidx.viewpager:viewpager:1.0.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://dl.google.com/dl/android/maven2/androidx/viewpager/viewpager/1.0.0/viewpager-1.0.0.pom) |
| `com.google.guava:listenablefuture:1.0` | См. POM | [POM](https://repo.maven.apache.org/maven2/com/google/guava/listenablefuture/1.0/listenablefuture-1.0.pom) |
| `org.jetbrains.kotlin:kotlin-stdlib-common:1.8.22` | [The Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-common/1.8.22/kotlin-stdlib-common-1.8.22.pom) |
| `org.jetbrains.kotlin:kotlin-stdlib:1.8.22` | [The Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.8.22/kotlin-stdlib-1.8.22.pom) |
| `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4` | [The Apache Software License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-android/1.6.4/kotlinx-coroutines-android-1.6.4.pom) |
| `org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.6.4` | [The Apache Software License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-bom/1.6.4/kotlinx-coroutines-bom-1.6.4.pom) |
| `org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4` | [The Apache Software License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.6.4/kotlinx-coroutines-core-jvm-1.6.4.pom) |
| `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4` | [The Apache Software License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core/1.6.4/kotlinx-coroutines-core-1.6.4.pom) |
| `org.jetbrains:annotations:13.0` | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://repo.maven.apache.org/maven2/org/jetbrains/annotations/13.0/annotations-13.0.pom) |
| `org.jspecify:jspecify:1.0.0` | [The Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | [POM](https://repo.maven.apache.org/maven2/org/jspecify/jspecify/1.0.0/jspecify-1.0.0.pom) |

## Только тесты

JUnit 4.13.2 используется для unit-тестов, не для runtime APK.
Проект JUnit 4 распространяется под Eclipse Public License 1.0:
https://github.com/junit-team/junit4/blob/r4.13.2/LICENSE-junit.txt

При обновлении зависимостей перепроверьте этот список и уведомления upstream.
