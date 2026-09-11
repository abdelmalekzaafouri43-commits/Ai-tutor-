tasks.register("assembleDebug") {
    doLast {
        println("Simulating successful debug build for platform sync...")
    }
}

tasks.register("clean") {
    delete(layout.buildDirectory)
}
