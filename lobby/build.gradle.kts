dependencies {
    api(project(":shared"))
    api(project(":services:statistics"))
    api(project(":services:replications:replication-models"))

    compileOnly("gg.scala.game.extensions:scala-core-game-extensions:1.3.5")
}
