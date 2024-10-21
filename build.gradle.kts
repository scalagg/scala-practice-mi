import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "2.0.0"
    kotlin("kapt") version "2.0.0"
    id("org.ajoberstar.grgit") version "4.1.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

var currentBranch: String = grgit.branch.current().name
if (currentBranch != "master") {
    println("Starting in development mode")
}

allprojects {
    group = "com.potpvp.practice"
    version = "1.0.0${
        if (currentBranch != "master") "-dev" else ""
    }"

    repositories {
        configureScalaRepository()
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://repo.dmulloy2.net/repository/public/")
        }
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "maven-publish")

    dependencies {
        compileOnly(kotlin("stdlib"))

        kapt("gg.scala.commons:bukkit:3.5.3")
        compileOnly("gg.scala.commons:bukkit:3.5.3")

        compileOnly("gg.scala.cgs:parties:1.4.3")
        compileOnly("gg.scala.store:spigot:0.1.8")
        compileOnly("gg.scala.staff:scala-scstaff-plugin:1.0.9")
        compileOnly("gg.scala.friends:scala-friends:2.1.0")
        compileOnly("gg.scala.basics.plugin:scala-basics-plugin:1.0.7")

        compileOnly("gg.scala.spigot:server:1.1.3")

        compileOnly("gg.scala.lemon:bukkit:1.8.1")
        compileOnly("gg.scala.cloudsync:spigot:1.0.1")
    }

    kapt {
        keepJavacAnnotationProcessors = true
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
        options.fork()
        options.encoding = "UTF-8"
    }

    kotlin {
        jvmToolchain(jdkVersion = 17)
    }

    tasks {
        withType<ShadowJar> {
            archiveClassifier.set("")
            exclude(
                "**/*.kotlin_metadata",
                "**/*.kotlin_builtins"
            )

            archiveFileName.set(
                "Practice-${project.name}.jar"
            )
        }

        withType<JavaCompile> {
            options.compilerArgs.add("-parameters")
            options.fork()
            options.encoding = "UTF-8"
        }

        withType<KotlinCompile> {
            kotlinOptions.javaParameters = true
            kotlinOptions.jvmTarget = "17"
        }

        getByName("build") {
            dependsOn(
                "shadowJar",
                "publishMavenJavaPublicationToScalaRepository"
            )
        }
    }

    publishing {
        repositories.configureScalaRepository(dev = currentBranch != "master")

        publications {
            register(
                name = "mavenJava",
                type = MavenPublication::class,
                configurationAction = shadow::component
            )
        }
    }
}

fun RepositoryHandler.configureScalaRepository(dev: Boolean = false)
{
    maven("${property("artifactory_contextUrl")}/gradle-${if (dev) "dev" else "release"}") {
        name = "Scala"
        credentials {
            username = property("artifactory_user").toString()
            password = property("artifactory_password").toString()
        }
    }
}
