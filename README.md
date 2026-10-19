# Stealth App Hider

Android-прототип на Kotlin и Jetpack Compose с темным интерфейсом, PIN-входом, списком приложений и экраном настроек.

> Важно: обычное Android-приложение не может произвольно скрывать другие приложения из системного лаунчера. Этот проект является безопасным UI-прототипом: переключатели сохраняют состояние только внутри приложения и не изменяют установленные приложения.

## Возможности

- темная Material 3 тема;
- экран входа по PIN-коду;
- список демонстрационных приложений с переключателями «Скрыть/Показать»;
- сохранение PIN и состояний переключателей в локальных настройках;
- экран настроек с изменением PIN и демонстрационным переключателем биометрии;
- сборка debug APK через GitHub Actions без Android Studio.

Начальный PIN-код: `1234`.

## Структура проекта

```text
.
├── .github/
│   └── workflows/
│       └── build.yml
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/stealthapphider/
│       │   ├── MainActivity.kt
│       │   └── ui/theme/Theme.kt
│       └── res/values/styles.xml
├── build.gradle.kts
├── gradle.properties
├── gradle/wrapper/
│   ├── gradle-wrapper.jar
│   └── gradle-wrapper.properties
├── settings.gradle.kts
└── README.md
```

## Создание репозитория на GitHub

1. Откройте [GitHub](https://github.com/) и войдите в аккаунт.
2. Нажмите **New repository**.
3. Укажите имя, например `stealth-app-hider`.
4. Выберите видимость репозитория.
5. Не добавляйте README и `.gitignore`, если они уже есть в этой папке.
6. Нажмите **Create repository**.

## Пошаговая загрузка проекта на GitHub

### Шаг 1. Создайте репозиторий

1. Откройте [GitHub](https://github.com/) и войдите в аккаунт.
2. Нажмите **+** в правом верхнем углу, затем **New repository**.
3. Введите имя, например `stealth-app-hider`.
4. Выберите **Public** или **Private**.
5. Не устанавливайте флажки **Add a README file**, **Add .gitignore** и **Choose a license** — эти файлы уже находятся в проекте.
6. Нажмите **Create repository**.

### Шаг 2. Загрузите файлы проекта

Установите Git, если он еще не установлен, откройте PowerShell или терминал в корневой папке проекта и выполните:

```bash
git init
git add .
git commit -m "Initial Android Compose app"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/stealth-app-hider.git
git push -u origin main
```

Замените `YOUR_USERNAME` на имя пользователя GitHub и при необходимости измените URL репозитория. Введите учетные данные GitHub, если Git запросит их.

### Шаг 3. Проверьте запуск сборки

1. Откройте страницу созданного репозитория на GitHub.
2. Перейдите во вкладку **Actions**.
3. В списке слева выберите **Build Android APK**.
4. Откройте запуск, который появился после `git push`.
5. Дождитесь статуса **Success**. Android Studio и Android SDK на вашем компьютере для этого не нужны: сборка выполняется на GitHub Actions.

Для последующих изменений измените файлы проекта и выполните:

```bash
git add .
git commit -m "Update app"
git push
```

Каждый новый push в ветку `main` снова запустит workflow и создаст новый APK-артефакт.

## Сборка APK через GitHub Actions

Файл `.github/workflows/build.yml` запускается при каждом push в ветку `main` и вручную через **Run workflow**. Workflow:

1. получает код из репозитория;
2. устанавливает JDK 17;
3. запускает `./gradlew assembleDebug`;
4. загружает `app-debug.apk` через `actions/upload-artifact@v3` как артефакт `stealth-app-hider-debug-apk`.

Параметры Android-модуля настроены так: `minSdk = 24`, `targetSdk = 34`, `compileSdk = 34`. Репозитории Google и Maven Central указаны в корневом Gradle-файле и в `settings.gradle.kts`.

Локальный Android SDK и Android Studio для GitHub Actions не требуются.

## Где скачать готовый APK

После успешной сборки:

1. Откройте репозиторий на GitHub.
2. Перейдите во вкладку **Actions**.
3. Нажмите workflow **Build Android APK**.
4. Выберите нужный успешный запуск с зеленой галочкой **Success**.
5. Прокрутите страницу запуска вниз до раздела **Artifacts**.
6. Нажмите `stealth-app-hider-debug-apk`.
7. Скачанный ZIP-архив распакуйте на компьютере — внутри находится `app-debug.apk`.
8. Передайте APK на Android-устройство и установите его для тестирования.

APK из debug-сборки предназначен для тестирования. Перед публикацией приложения потребуется настроить подпись release-сборки и проверить приложение на реальном устройстве.
