import com.android.build.gradle.internal.cxx.cmake.TargetDataItem

plugins {
    id("com.android.application") version "7.3.0" apply false
    // ...

    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.4" apply false
}

android {
    namespace = "com.example.fireinaction"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fireinaction"
        minSdk = 24 // Un valor más común para alcanzar a más dispositivos.
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        // 'prefab' se suele usar para dependencias nativas (C/C++).
        // Si no usas bibliotecas .aar con código C/C++, puedes eliminar esta línea.
        prefab = true
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            // La versión de CMake se gestiona de forma centralizada, por lo que
            // no es necesario especificarla aquí.
        }
    }
}

// ▼▼▼ ÚNICO BLOQUE DE DEPENDENCIAS ▼▼▼
dependencies {
    // --- Dependencias de AndroidX existentes ---
    // (Estas son las líneas que movimos desde el bloque incorrecto)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.games.activity)
    // También es muy común tener la de Material Design aquí
    implementation(libs.material) // <- Asegúrate de que esta dependencia esté en tu `libs.versions.toml`

}
dependencies {

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))

    // When using the BoM, you don't specify versions in Firebase library dependencies

    // Add the dependency for the Firebase SDK for Google Analytics
    implementation("com.google.firebase:firebase-analytics")

    // TODO: Add the dependencies for any other Firebase products you want to use
    // See https://firebase.google.com/docs/android/setup#available-libraries
    // For example, add the dependencies for Firebase Authentication and Cloud Firestore
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
}

// --- 🚀 Dependencias Adicionales para Juegos/Rendimiento 🚀 ---

// Para la lógica de navegación (útil para moverte entre modos/pantallas)
var implementation=("androidx.navigation:navigation-fragment-ktx:2.7.7")
var implementation = ("androidx.navigation:navigation-ui-ktx:2.7.7")






