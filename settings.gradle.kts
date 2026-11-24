pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "hintfix"

// One subproject per supported Minecraft version; all of them build the same shared sources in src/.
for (mc in listOf("1.21.11", "26.2", "26.3")) {
    include(mc)
    project(":$mc").projectDir = file("versions/$mc")
}
