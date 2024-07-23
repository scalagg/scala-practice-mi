plugins {
    id("io.freefair.lombok") version "5.3.0"
}

dependencies {
    compileOnly(project(":game"))
    compileOnly(project(":lobby"))
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INHERIT
}
