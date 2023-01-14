import org.gradle.kotlin.dsl.support.zipTo
import org.gradle.util.TextUtil.normaliseFileSeparators

buildscript {
    repositories {
        mavenCentral()
        google()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath(libs.gradle.agp)
        classpath(libs.gradle.kotlin)
        classpath(libs.gradle.serialization)
        classpath(libs.gradle.kotlinter)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven(url = "https://jitpack.io")
    }
}

tasks.register("buildOutput") {
    doLast {
        mkdir("./build/output")
        val file = File(rootDir, "src")
        for (dir in file.listFiles()!!) {
            if (!dir.isDirectory) continue
            for (subdir in dir.listFiles()!!) {
                if (!subdir.isDirectory) continue
                val id = "${dir.name}.${subdir.name}"
                val outDir = "./build/output/$id"
                // create tmp outdir
                mkdir(outDir)
                // copy files to output dir
                copy {
                    from("${subdir.path}/build/distributions/")
                    include("${subdir.name}.js", "web_hi_res_512.png", "source.json")
                    into(outDir)
                    rename("${subdir.name}.js", "main.js")
                    rename("web_hi_res_512.png", "Icon.png")
                }
                // zip files into aix
                val files = sequenceOf(
                    File("$outDir/main.js"),
                    File("$outDir/Icon.png")
                )
                val entries = files.map { f ->
                    val path = f.relativeTo(File(outDir)).path
                    val bytes = f.readBytes()
                    normaliseFileSeparators(path) to bytes
                }
                zipTo(File("./build/output/$id.aix"), entries)
                // delete tmp outdir
                delete(outDir)
            }
        }
    }
}
