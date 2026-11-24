# HintFix

fabric mod for minecraft 1.21.11, 26.2 and 26.3 that turns off freetype hinting, so truetype fonts never get to run
their hinting bytecode, mainly to protect against the recent minecraft2shell vulnerablity 

## building

`./gradlew build` builds every supported version; jars end up in `versions/<mc>/build/libs/`.
a single version can be built with e.g. `./gradlew :1.21.11:build`.

all versions share the same sources in `src/`. per-version settings (minecraft version, java version)
live in `versions/<mc>/gradle.properties`; to add a version, create that file and list it in `settings.gradle.kts`.
