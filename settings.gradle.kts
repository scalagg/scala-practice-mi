pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "esta-practice"
include(
    "shared", "game", "lobby", "devtools", "sbb",
    "services:application", "services:queue", "services:statistics", "services:orchestrator", "services:tournaments",
    "services:statistics:leaderboards",
    "services:application:api",
    "services:replications:replication-manager",
    "services:replications:replication-models",
    "services:games:game-manager",
    "minigames:skywars",
    "minigames:skywars-shared",
)
