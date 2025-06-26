repositories {
    maven("https://repo.glaremasters.me/repository/concuncan/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.lunarclient.dev")
}

dependencies {
    compileOnly("gg.scala.spa.plugin:scala-spa-plugin:1.1.4")

    api(project(":shared"))
    api(project(":services:statistics"))
    api(project(":services:replications:replication-models"))

    compileOnly("dev.cubxity.plugins:unifiedmetrics-api:0.3.8")
    compileOnly("com.grinderwolf:slimeworldmanager-plugin:2.2.1")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")

    compileOnly("gg.scala.game.extensions:scala-core-game-extensions:1.3.5")

    compileOnly("com.lunarclient:apollo-api:1.1.8")
    compileOnly("com.lunarclient:apollo-extra-adventure4:1.1.8")
}

