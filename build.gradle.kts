plugins {
    id("net.minecraftforge.gradle") version "[6.0.16,6.2)"
    id("eclipse")
    id("maven-publish")
}

version = "1.0.0"
group = "com.itemslot"
base.archivesName.set("item-slot-forge-1.20.1")

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

// Sources and resources contain non-ASCII characters; the platform default
// encoding on Windows (GBK) would otherwise mangle them during compilation.
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<ProcessResources>().configureEach {
    filteringCharset = "UTF-8"
}

minecraft {
    mappings("official", "1.20.1")

    runs {
        create("client") {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", "item_slot")
            mods.create("item_slot") { source(sourceSets.main.get()) }
        }
        create("server") {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", "item_slot")
            mods.create("item_slot") { source(sourceSets.main.get()) }
        }
    }
}

dependencies {
    minecraft("net.minecraftforge:forge:1.20.1-47.3.0")
}
