repositories {
    mavenCentral()
    maven("https://repo.glaremasters.me/repository/concuncan/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    api(project(":shared"))
    compileOnly(fileTree("worldedit"))

    /*    implementation("com.github.haifengl:smile-core:2.3.0")
        implementation("com.github.haifengl:smile-kotlin:2.3.0")*/

    compileOnly("com.grinderwolf:slimeworldmanager-plugin:2.2.1")
}

