repositories {
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    compileOnly(project(":game"))
    compileOnly(project(":shared"))
    api(project(":minigames:skywars-shared"))
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("gg.tropic.game.extensions:tropic-core-game-extensions:1.3.0")
}
