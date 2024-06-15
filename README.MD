# opsu! ~ an open-source osu! client

![машрумс](https://img.shields.io/badge/%D0%BC%D0%B0%D1%88%D1%80%D1%83%D0%BC%D1%81-1f1f1f?style=flat-square) ![мастхэв](https://img.shields.io/badge/%D0%BC%D0%B0%D1%81%D1%82%D1%85%D1%8D%D0%B2-1f1f1f?style=flat-square) ![брэд](https://img.shields.io/badge/%D0%B1%D1%80%D1%8D%D0%B4-1f1f1f?style=flat-square) ![спишь](https://img.shields.io/badge/%D1%81%D0%BF%D0%B8%D1%88%D1%8C-1f1f1f?style=flat-square)

![0% optimized](https://img.shields.io/badge/0%25-optimized-green?style=flat-square) ![does it even work?](https://img.shields.io/badge/works%20on-my%20machine-red?style=flat-square)

A fork of [fork](https://github.com/fluddokt/opsu) of [opsu!](https://github.com/itdelatrisu/opsu) that use libgdx to fake Slick2D's API and do it very bad.
I do some shit here. Don't judge me, please.

## Building
Cutting-edge builds are generated automatically for every commit. You can see them in the [actions](https://github.com/rust2/opsu/actions) tab (you have to be logged in).

![Actions](readme/finding-actions-how-to.png)

If you'd rather compile on your own, follow these instructions.
First, make sure you have at least [JDK 8](https://adoptium.net/temurin/archive/?version=8) installed.

Then clone the project by command `git clone https://github.com/rust2/opsu.git`

Open a command line or a terminal in the root directory of the project and run the following commands:

### Windows

_Run:_ `gradlew desktop:run`  
_Build:_ `gradlew desktop:dist`  

### Linux/Mac OS

_Run:_ `./gradlew desktop:run`  
_Build:_ `./gradlew desktop:dist`  

### Android (If you use Intellij IDEA or Android Studio) `//fixme`
#### P.S. I'm noob at this theme...

1. Enable Android plugin.
![Actions](readme/Intellij-enable-android-plugin.png)
2. When importing a project, IDEA will download needed SDK and platform-tools.
3. Set the `ANDROID_HOME` environment variable to point to your unzipped Android SDK directory.
7. Enable developer mode on your device/emulator. If you are on testing on a phone you can follow [these instructions](https://developer.android.com/studio/command-line/adb#Enabling), otherwise you need to google how to enable your emulator's developer mode specifically.
8. Run `gradlew android:assembleDebug` (or `./gradlew` if on linux/mac). This will create an unsigned APK in `android/build/outputs/apk`.

## If something refuses to work...
If your IDE throws an error while trying to launch a desktop project, go to project folder and create there a `run` folder.
>On one PC I had to create the folder myself, on another it was created automatically (as it supposed to be).

## [Credits](https://github.com/fluddokt/opsu/blob/master/CREDITS.md)
