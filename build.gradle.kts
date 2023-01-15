import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

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

fun zipFiles(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String) {
    val data = ByteArray(2048)

    for (f in sourceFile.listFiles()!!) {
        if (f.isDirectory) {
            val entry = ZipEntry(f.name + File.separator)
            entry.time = f.lastModified()
            entry.isDirectory
            entry.size = f.length()
            zipOut.putNextEntry(entry)
            zipFiles(zipOut, f, f.name)
        } else {
            java.io.FileInputStream(f).use { fi ->
                java.io.BufferedInputStream(fi).use { origin ->
                    val path = parentDirPath + File.separator + f.name
                    val entry = ZipEntry(path)
                    entry.time = f.lastModified()
                    entry.isDirectory
                    entry.size = f.length()
                    zipOut.putNextEntry(entry)
                    while (true) {
                        val readBytes = origin.read(data)
                        if (readBytes == -1) {
                            break
                        }
                        zipOut.write(data, 0, readBytes)
                    }
                }
            }
        }
    }
}

fun zipDir(directory: String, zipFile: String) {
    val sourceFile = File(directory)
    if (!sourceFile.isDirectory) return

    ZipOutputStream(java.io.BufferedOutputStream(java.io.FileOutputStream(zipFile))).use { out ->
        out.use {
            val entry = ZipEntry(sourceFile.name + File.separator)
            entry.time = sourceFile.lastModified()
            entry.isDirectory
            entry.size = sourceFile.length()
            it.putNextEntry(entry)
            zipFiles(it, sourceFile, sourceFile.name)
        }
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
                val outDir = "./build/output/Payload"
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
                zipDir(outDir, "./build/output/Payload.zip")
                // rename Payload.zip
                copy {
                    from("./build/output")
                    include("Payload.zip")
                    into("./build/output")
                    rename("Payload.zip", "$id.aix")
                }
                // delete Payload.zip
                delete("$outDir.zip")
                // delete tmp outdir
                delete(outDir)
            }
        }
    }
}
