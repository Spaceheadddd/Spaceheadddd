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

## Features

- **Thermal gauge** — live battery temperature, target marker, inner power ring,
  red ticks at the safety cutoff.
- **Heat monitor** — 12-minute temperature trace with target line and power bars.
- **Telemetry** — room, outside, power draw (W), battery %, thermal headroom,
  thermal status, warming rate (°C/min), session peak, time remaining.
- **Fast heating** — full power until ~0.5 °C from target, with a trend
  prediction (battery temperature lags the chip) to avoid overshooting. Workers
  run at normal priority so they land on the big cores, and stream memory as
  well as doing math.
- **TURBO** — also loads the GPU with an offscreen shader for the fastest warm-up.
- **Room temperature** — estimated from the phone alone: a background job
  samples the battery temperature every ~15 min while the phone sits idle
  (screen off, not charging, not heating) and subtracts a small offset. `~21°`
  means idle-based, `≈21°` is a rougher live guess. Tap ROOM to calibrate once
  against a real thermometer.
- **Outside temperature** — ambient sensor if present, otherwise current local
  weather from [Open-Meteo](https://open-meteo.com) via coarse location.
- **Modes** — GENTLE 36 °C, WARM 40 °C, BLAZE 44 °C, or **AUTO** (target follows
  the weather: ≤ −5 °C outside → 44 °C, ≥ 15 °C → 36 °C).
- **⚠ OVERDRIVE** — unlocks 45–46 °C behind an education screen and explicit
  consent; cutoff 48 °C, sessions capped at 20 min, resets every app launch.
- **Haptic pulse** when the phone first reaches temperature.
- **Quick Settings tile** — pull down the shade, edit tiles, add *Hand Warmer*.
- **Arcane Labs** 5.5 s boot sequence; dark, monospace, minimal UI.

## Get the APK

Every push that touches `hand-warmer/` builds it on GitHub Actions:
**Actions → Build Hand Warmer APK → latest run → Artifacts → hand-warmer-apk**.
Unzip, copy `app-debug.apk` to the phone and open it. Builds from v1.1 on are
signed with a fixed debug key (`app/debug.keystore`), so new versions install
over old ones; if you installed v1.0, uninstall it once first (allow "Install unknown
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
