import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    // 26.x is unobfuscated (plain loom); 1.21.x is obfuscated and needs the remapping variant.
    id("net.fabricmc.fabric-loom") version "1.19-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom-remap") version "1.19-SNAPSHOT" apply false
}

subprojects {
    val mc = property("minecraft_version") as String
    val javaVersion = (property("java_version") as String).toInt()
    val obfuscated = (property("obfuscated") as String).toBoolean()
    val loaderVersion = property("loader_version") as String
    val minecraftDependency = property("minecraft_dependency") as String

    apply(plugin = if (obfuscated) "net.fabricmc.fabric-loom-remap" else "net.fabricmc.fabric-loom")

    group = rootProject.property("group") as String
    version = "${rootProject.property("mod_version")}+$mc"

    extensions.configure<BasePluginExtension> { archivesName = "hintfix" }

    // The mixin only targets LWJGL's FreeType bindings, whose signatures are identical across
    // LWJGL 3.3.3 (1.21.11), 3.4.1 (26.2) and 3.4.3 (26.3), so every version shares one source set.
    extensions.configure<SourceSetContainer> {
        named("main") {
            java.setSrcDirs(listOf(rootProject.file("src/main/java")))
            resources.setSrcDirs(listOf(rootProject.file("src/main/resources")))
        }
    }

    dependencies {
        "minecraft"("com.mojang:minecraft:$mc")
        if (obfuscated) {
            "mappings"(project.extensions.getByType<LoomGradleExtensionAPI>().officialMojangMappings())
            "modImplementation"("net.fabricmc:fabric-loader:$loaderVersion")
        } else {
            "implementation"("net.fabricmc:fabric-loader:$loaderVersion")
        }
    }

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion = JavaLanguageVersion.of(25)
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release = javaVersion
    }

    tasks.named<ProcessResources>("processResources") {
        val props = mapOf(
            "version" to version.toString(),
            "loader_version" to loaderVersion,
            "minecraft_dependency" to minecraftDependency,
            "java_version" to javaVersion.toString(),
        )
        inputs.properties(props)
        filesMatching(listOf("fabric.mod.json", "hintfix.mixins.json")) { expand(props) }
    }
}
