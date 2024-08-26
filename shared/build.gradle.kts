repositories {
    maven("https://repo.glaremasters.me/repository/concuncan/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    implementation(project(":services:replications:replication-models"))
    api(project(":services:statistics"))

    compileOnly("gg.tropic.game.extensions:tropic-core-game-extensions:1.3.0")
    compileOnly("gg.scala.achievements:scala-achievements-plugin:1.0.5")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0")

    api("joda-time:joda-time:2.12.5")
    api("xyz.xenondevs:particle:1.7.1")
}
