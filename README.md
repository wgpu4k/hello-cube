# wgpu4k Hello Cube Sample

Minimal project to show how to use wgpu4k 

## How to Run the Project

1. On desktop: `./gradlew runJvm`
2. On web js: `./gradlew jsRun`
3. On web wasm: `./gradlew wasmJsRun`
4. On native MacOs: `./gradlew runReleaseExecutableOsx`
5. On Android, run the subproject `android` with android studio ![android-studio-capture.png](android-studio-capture.png)
6. On iOS `./gradlew hello-cube:assembleWgpuAppXCFramework` to build the XC Framework, then you can run the subproject `iosApp` (on hello-cube folder) with XCode on a iOS simulator or real device.


## Screenshot
![Rotating cube](desktop-catpure.gif)
![Rotating cube on mobile](mobile-capture.gif)