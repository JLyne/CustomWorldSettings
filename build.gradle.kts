import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "1.4.0"
  id("xyz.jpenilla.run-paper") version "2.0.1" // Adds runServer and runMojangMappedServer tasks for testing
  id("net.minecrell.plugin-yml.bukkit") version "0.5.2" // Generates plugin.yml
}

group = "uk.co.notnull"
version = "1.0.0-SNAPSHOT"
description = "Simple plugin for changing dimension type settings on the RTGame Build server"

java {
  // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
  toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    maven("https://repo.purpurmc.org/snapshots")
}

dependencies {
  paperweightDevBundle("org.purpurmc.purpur", "1.19.4-R0.1-SNAPSHOT")
  // paperweightDevBundle("com.example.paperfork", "1.19.3-R0.1-SNAPSHOT")

  // You will need to manually specify the full dependency if using the groovy gradle dsl
  // (paperDevBundle and paperweightDevBundle functions do not work in groovy)
  // paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.19.3-R0.1-SNAPSHOT")
}

tasks {
  // Configure reobfJar to run when invoking the build task
  assemble {
    dependsOn(reobfJar)
  }

  compileJava {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

    // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
    // See https://openjdk.java.net/jeps/247 for more information.
    options.release.set(17)
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
  }
  processResources {
    filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
  }

  /*
  reobfJar {
    // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
    // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
    outputJar.set(layout.buildDirectory.file("libs/PaperweightTestPlugin-${project.version}.jar"))
  }
   */
}

// Configure plugin.yml generation
bukkit {
  main = "uk.co.notnull.customworldsettings.CustomWorldSettings"
  apiVersion = "1.19"
  authors = listOf("Author")
  load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
}
