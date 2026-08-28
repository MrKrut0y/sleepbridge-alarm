# Sleepbridge Alarm

Минимальный Android/Kotlin-прототип для сценария:

`Gadgetbridge + Xiaomi Smart Band 9 обнаружили сон -> прибавляем заданную длительность сна -> ставим Android-будильник -> по возможности ставим будильник на браслете через Gadgetbridge`

## Что проверено в Gadgetbridge

- В Gadgetbridge есть `Device actions`: при обнаружении сна устройство может отправить Android broadcast. Стандартная строка события засыпания в актуальных исходниках: `nodomain.freeyourgadget.gadgetbridge.FellAsleep`.
- В актуальной документации и исходниках Codeberg есть no-root alarm intent API:
  - `nodomain.freeyourgadget.gadgetbridge.command.SET_ALARM`
  - `nodomain.freeyourgadget.gadgetbridge.command.DISMISS_ALARM`
  - extras: `device`, `hour`, `minutes`, `title`, для удаления по названию `mode=title`
- Xiaomi Smart Band 9 в Gadgetbridge все еще помечен как experimental, поэтому надежность live-события сна зависит от версии Gadgetbridge, прошивки браслета и того, умеет ли конкретная связка отдавать `SLEEP_STATE_DETECTION`.

Источники:

- https://gadgetbridge.org/internals/automations/events/
- https://gadgetbridge.org/internals/automations/intents/
- https://gadgetbridge.org/internals/automations/intents/alarms/
- https://codeberg.org/Freeyourgadget/Gadgetbridge/src/branch/master/app/src/main/java/nodomain/freeyourgadget/gadgetbridge/externalevents/DeviceAlarmReceiver.java
- https://codeberg.org/Freeyourgadget/Gadgetbridge/src/branch/master/app/src/main/java/nodomain/freeyourgadget/gadgetbridge/devices/xiaomi/watches/MiBand9Coordinator.java

## Архитектура приложения

- `MainActivity` - простой экран настроек и кнопка теста.
- `SleepWatchService` - foreground-сервис, который динамически слушает broadcast от Gadgetbridge.
- `SleepEventReceiver` - запасной manifest-приемник и тестовая точка входа.
- `SleepAlarmEngine` - собирает сценарий целиком после события сна.
- `SleepAlarmCalculator` - чистый расчет времени будильника.
- `AndroidAlarmScheduler` - ставит собственный in-app alarm и, опционально, будильник через Android Clock.
- `GadgetbridgeAlarmClient` - общается с Gadgetbridge только через публичные broadcasts.

## Настройка на телефоне

1. Установи актуальный Gadgetbridge: обычную сборку, F-Droid или nightly.
2. Подключи Xiaomi Smart Band 9 в Gadgetbridge.
3. В Gadgetbridge открой настройки браслета.
4. Открой `Device actions`.
5. Для `On Fall Asleep` выбери `Send Broadcast`.
6. Укажи broadcast message:

   ```text
   nodomain.freeyourgadget.gadgetbridge.FellAsleep
   ```

7. Для вибрации/будильника на браслете включи в настройках устройства Gadgetbridge разрешение для сторонних приложений ставить будильники: обычно это `Developer settings -> allow 3rd party apps to set alarms`.
8. В Sleepbridge Alarm введи MAC-адрес браслета так, как он показан в Gadgetbridge.

## Сборка

Открой папку проекта в Android Studio:

```text
C:\Users\User\Documents\Codex\2026-08-29\referenced-chatgpt-conversation-this-is-an\outputs\sleepbridge-alarm
```

Или собери из терминала, если установлен Android SDK:

```powershell
.\gradlew.bat assembleDebug
```

Установка APK:

```powershell
adb install .\app\build\outputs\apk\debug\app-debug.apk
```

## Проверка без ожидания сна

Открой приложение, нажми `Save and start`, затем нажми `Test sleep event`.

ADB-вариант:

```powershell
adb shell am broadcast -a dev.sleepbridge.alarm.TEST_FELL_ASLEEP -p dev.sleepbridge.alarm
```

Если MAC и разрешение Gadgetbridge настроены, приложение дополнительно отправит в Gadgetbridge:

```text
DISMISS_ALARM mode=title title="Sleepbridge Alarm"
SET_ALARM hour=<calculated> minutes=<calculated> title="Sleepbridge Alarm"
```

## Ограничения

- Приложение не читает приватную SQLite-базу Gadgetbridge. Это намеренно: обычному Android-приложению без root/LSPosed такой доступ недоступен.
- Прототип зависит от live-события сна в Gadgetbridge, а не от постфактум-экспорта сна.
- Android может попросить разрешение на уведомления, потому что слушатель работает как foreground-сервис.
- Создание Android Clock alarm зависит от установленного приложения часов. Параллельно прототип ставит собственный in-app alarm как запасной вариант.
- Gadgetbridge не возвращает structured success/failure для alarm API; ошибки видны в логах Gadgetbridge.
