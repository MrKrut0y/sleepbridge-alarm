# Sleepbridge Alarm

Android-прототип будильника, который связывает Gadgetbridge и Xiaomi Smart Band 9.

Приложение получает событие начала сна от Gadgetbridge, прибавляет заданную длительность и ставит будильник на телефоне. При наличии MAC-адреса браслета и разрешения Gadgetbridge оно также создаёт будильник на Xiaomi Smart Band 9.

## Возможности

- live-событие `FellAsleep` от Gadgetbridge;
- настраиваемая длительность сна в часах и минутах;
- точный внутренний будильник Android с экраном, выбранным аудиофайлом и вибрацией;
- дополнительный будильник на браслете через публичный intent API Gadgetbridge;
- foreground-сервис для ожидания события сна;
- тестовое событие из приложения или через ADB;
- восстановление слушателя после перезагрузки телефона.

## Как это работает

```text
Xiaomi Smart Band 9
        |
        v
Gadgetbridge: FellAsleep broadcast
        |
        v
Sleepbridge Alarm: время сна + заданная длительность
        |
        +--> Android AlarmManager + выбранный аудиофайл
        |
        +--> Gadgetbridge SET_ALARM -> браслет
```

Приложение не читает приватную базу данных Gadgetbridge и не требует root или LSPosed.

## Требования

- Android Studio с Android SDK;
- Android 8.0 или новее;
- установленный Gadgetbridge;
- Xiaomi Smart Band 9, подключённый к Gadgetbridge.

## Настройка Gadgetbridge

1. Открой настройки Xiaomi Smart Band 9 в Gadgetbridge.
2. Перейди в `Device actions`.
3. Для `On Fall Asleep` выбери `Send Broadcast`.
4. В поле broadcast message укажи:

   ```text
   nodomain.freeyourgadget.gadgetbridge.FellAsleep
   ```

5. Если нужен будильник на браслете, разреши сторонним приложениям устанавливать будильники в настройках устройства Gadgetbridge. Обычно это находится в `Developer settings`.
6. Запусти Sleepbridge Alarm, укажи длительность сна и MAC-адрес браслета.
7. Нажми `Save and start`.

## Сборка и установка

Открой корневую папку проекта в Android Studio и дождись синхронизации Gradle. Затем выбери телефон или эмулятор и нажми `Run`.

Системное приложение «Часы» не требуется. В настройках Sleepbridge Alarm нажми `Choose alarm audio` и выбери любой аудиофайл на телефоне. Если файл не выбран, будет использован системный звук будильника.

Из терминала Windows:

```powershell
.\gradlew.bat assembleDebug
adb install .\app\build\outputs\apk\debug\app-debug.apk
```

## Быстрая проверка

После запуска приложения нажми `Save and start`, затем `Test sleep event`.

Или отправь тестовое событие через ADB:

```powershell
adb shell am broadcast -a dev.sleepbridge.alarm.TEST_FELL_ASLEEP -p dev.sleepbridge.alarm
```

Через несколько секунд приложение должно запланировать будильник на текущее время плюс заданную длительность.

## Архитектура

- `MainActivity` хранит настройки и запускает слушатель.
- `SleepWatchService` принимает события Gadgetbridge в foreground-режиме.
- `SleepAlarmEngine` координирует расчёт и установку будильников.
- `SleepAlarmCalculator` содержит чистую логику расчёта времени.
- `AndroidAlarmScheduler` работает с Android AlarmManager и системным приложением часов.
- `GadgetbridgeAlarmClient` отправляет публичные intent-команды Gadgetbridge.

## Ограничения прототипа

- точность зависит от того, когда конкретная версия Gadgetbridge и прошивка браслета отправляют событие сна;
- Android может потребовать разрешение на уведомления для foreground-сервиса;
- приложение использует собственный `AlarmManager`, поэтому системное приложение «Часы» не требуется;
- Gadgetbridge не возвращает приложению структурированный результат установки будильника.

## Источники

- [Gadgetbridge: device actions](https://gadgetbridge.org/internals/automations/events/)
- [Gadgetbridge: intents](https://gadgetbridge.org/internals/automations/intents/)
- [Gadgetbridge: alarm intents](https://gadgetbridge.org/internals/automations/intents/alarms/)
- [Gadgetbridge source: DeviceAlarmReceiver](https://codeberg.org/Freeyourgadget/Gadgetbridge/src/branch/master/app/src/main/java/nodomain/freeyourgadget/gadgetbridge/externalevents/DeviceAlarmReceiver.java)
- [Gadgetbridge source: MiBand9Coordinator](https://codeberg.org/Freeyourgadget/Gadgetbridge/src/branch/master/app/src/main/java/nodomain/freeyourgadget/gadgetbridge/devices/xiaomi/watches/MiBand9Coordinator.java)

## Статус

Это минимально рабочий прототип. Перед регулярным использованием проверь срабатывание на своём телефоне, особенно при включённой оптимизации батареи и при перезагрузке устройства.
