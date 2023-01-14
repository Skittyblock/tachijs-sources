plugins {
    kotlin("js")
}

kotlin {
    js {
        binaries.executable()
        browser()
    }
}

//android {
//    compileSdk = AndroidConfig.compileSdk
//
//    defaultConfig {
//        minSdk = AndroidConfig.minSdk
//    }
//
//    sourceSets {
//        named("main") {
//            manifest.srcFile("AndroidManifest.xml")
//            res.setSrcDirs(listOf("res"))
//        }
//    }
//
//    libraryVariants.all {
//        generateBuildConfigProvider?.configure {
//            enabled = false
//        }
//    }
//}
