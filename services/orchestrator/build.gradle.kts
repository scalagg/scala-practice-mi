dependencies {
    compileOnly(project(":shared"))
    compileOnly(project(":services:application:api"))
    compileOnly(project(":minigames:skywars-shared"))

    implementation("net.md-5:bungeecord-chat:1.20-R0.1")
}
