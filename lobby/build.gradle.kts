dependencies {
    api(project(":shared"))
    api(project(":services:statistics"))
    api(project(":services:replications:replication-models"))

    compileOnly("gg.tropic.game.extensions:tropic-core-game-extensions:1.2.8")
}
