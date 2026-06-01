import java.util.Properties

plugins {
    id("com.android.application")
}

val signingPropertiesFile = rootProject.file("keystore/signing.properties")
val signingProperties = Properties().apply {
    if (signingPropertiesFile.exists()) {
        signingPropertiesFile.inputStream().use(::load)
    }
}

val targetAbis = providers.gradleProperty("nfcunlocker.targetAbis")
    .orElse("arm64-v8a")
    .get()
    .split(",")
    .map { it.trim() }
    .filter { it.isNotEmpty() }

if (targetAbis.isEmpty()) {
    throw GradleException("至少需要配置一个目标架构")
}

fun signingProperty(name: String): String =
    signingProperties.getProperty(name)
        ?: throw GradleException("缺少签名配置: ${signingPropertiesFile.path} 中的 $name")

android {
    namespace = "com.juren233.nfcunlocker"
    compileSdk = 36
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "com.juren233.nfcunlocker"
        minSdk = 21
        targetSdk = 35
        versionCode = 4
        versionName = "1.2.0"
    }

    splits {
        abi {
            isEnable = true
            reset()
            include(*targetAbis.toTypedArray())
            isUniversalApk = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    signingConfigs {
        create("formal") {
            storeFile = rootProject.file(signingProperty("storeFile"))
            storePassword = signingProperty("storePassword")
            keyAlias = signingProperty("keyAlias")
            keyPassword = signingProperty("keyPassword")
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("formal")
        }
        release {
            signingConfig = signingConfigs.getByName("formal")
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
}
