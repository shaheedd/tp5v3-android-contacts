import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// La clé Google Maps est lue depuis local.properties (fichier exclu du dépôt).
// Sans clé, l'application utilise une vue cartographique de repli.
val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}
val mapsApiKey = localProperties.getProperty("maps.api.key", "")

android {
    namespace = "com.example.contacts"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.contacts"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
        manifestPlaceholders["mapsApiKey"] = mapsApiKey
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.google.maps)
}
