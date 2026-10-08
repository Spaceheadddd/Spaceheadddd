# Hand Warmer (Android)

Turns your phone into a hand warmer by running the CPU, then **holds** it at a
comfortable temperature instead of letting it keep climbing. Built for (and
tested against the APIs of) the Galaxy S24 Ultra, works on Android 10+.

## How it works

- A foreground service starts one worker thread per CPU core.
- Each worker runs a duty cycle: busy for *duty* × 100 ms, then sleeps.
- Every 2 s a thermostat loop reads the **battery temperature** and sets the
  duty proportionally: full power well below target, tapering to zero just
  above it. That's what makes it *stay* warm rather than overheat.
- A partial wake lock keeps it going with the screen off / phone in a pocket.
- Optional **screen boost** keeps the display on at full brightness while the
  app is open, for extra heat.

## Safety limits (always on)

| Condition | Action |
| --- | --- |
| Battery ≥ 44 °C | stop |
| Android thermal status *severe* or worse | stop |
| Android thermal status *moderate* | cap at 50% power |
| Battery < 15% (unplugged) | stop |
| Plugged in / charging | pause (heat + charging hurts the battery) |
| Auto-off timer (10–120 min) | stop |

Target is adjustable from 32–42 °C (default 38 °C). Expect heavy battery drain:
roughly 15–30% per 30 min at full power on an S24 Ultra.

## Get the APK

Every push that touches `hand-warmer/` builds it on GitHub Actions:
**Actions → Build Hand Warmer APK → latest run → Artifacts → hand-warmer-apk**.
Unzip, copy `app-debug.apk` to the phone and open it (allow "Install unknown
apps" for your file manager or browser when prompted).

Or build locally with Android Studio / the Android SDK:

```sh
cd hand-warmer
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

## S24 Ultra tips

- Allow notifications when asked; the ongoing notification has a **Stop** button.
- If One UI kills it in the background: Settings → Apps → Hand Warmer →
  Battery → **Unrestricted**.
