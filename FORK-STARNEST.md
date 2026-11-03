# StarNest fork of amneziawg-android

Upstream: https://github.com/amnezia-vpn/amneziawg-android (base commit ff15093, v3.1.4)

Purpose: publish the `tunnel` module through JitPack so the vpn-android app can use
AmneziaWG **next to** `com.wireguard.android:tunnel` in the same APK.

## Changes vs upstream
- Native library renamed `libwg-go.so` -> `libawg-go.so` (Makefile output + soname,
  CMake target, `GoBackend` loads `"awg-go"`). The official WireGuard tunnel AAR also
  ships `libwg-go.so`; identical file names make APK packaging fail.
- Root-mode executables `libwg.so` / `libwg-quick.so` (awg / awg-quick, used only by
  `AwgQuickBackend`) are no longer built: they clash with the WireGuard AAR and are not
  needed for `GoBackend`. Submodules are therefore not needed to build.
- `maven-publish` for the release variant: `com.github.hung55555hung:amneziawg-android:<tag>`.
- `consumer-rules.pro` keeps the JNI class/methods for apps that minify.
- NDK pinned to 27.0.12077973 (installed by `jitpack.yml`).
- `awgHostAppPackageName` (gradle.properties) = package passed as ANDROID_PACKAGE_NAME.

Java packages are unchanged (`org.amnezia.awg.*`) and do not collide with `com.wireguard.*`.

## Release
    git tag v3.1.4-sn1 && git push origin v3.1.4-sn1
Then open https://jitpack.io/#hung55555hung/amneziawg-android and press "Get it"
for the tag (first build compiles Go for 4 ABIs; check the log if it times out).

## Local build (macOS)
Needs JDK 17, Android SDK with NDK 27.0.12077973 and CMake 3.22.1, plus `flock`
(`brew install flock`) and a GNU-compatible `sha256sum` (`brew install coreutils`).

    ./gradlew :tunnel:assembleRelease -x lint
    # AAR: tunnel/build/outputs/aar/tunnel-release.aar

## Syncing with upstream
    git remote add upstream https://github.com/amnezia-vpn/amneziawg-android
    git fetch upstream && git rebase upstream/master
Re-check after rebase: any new `libwg-go.so` reference, and Makefile GO_HASH entries.
