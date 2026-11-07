import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy
import java.util.Properties
import java.io.FileInputStream
import java.io.File

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.studioas.movieboxoss"
    minSdk = 24
    targetSdk = 36
    versionCode = 7
    versionName = "4.2"
  }

  val keystorePropertiesFile = rootProject.file("key.properties")
  val keystoreProperties = Properties()
  if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
  }

  val releaseKeyPath = keystoreProperties.getProperty("storeFile") ?: "release-key.jks"
  val resolvedReleaseKeyFile = File(releaseKeyPath)
  val releaseKeyFile = if (resolvedReleaseKeyFile.isAbsolute) resolvedReleaseKeyFile else file(releaseKeyPath)
  val hasReleaseKey = releaseKeyFile.exists()

  signingConfigs {
    if (hasReleaseKey) {
      create("release") {
        storeFile = releaseKeyFile
        storePassword = keystoreProperties.getProperty("storePassword")
        keyAlias = keystoreProperties.getProperty("keyAlias")
        keyPassword = keystoreProperties.getProperty("keyPassword")
      }
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = if (hasReleaseKey) {
        signingConfigs.getByName("release")
      } else {
        signingConfigs.getByName("debug")
      }
    }
    debug {
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
}



googleServices {
  missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN
}


// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  implementation("androidx.appcompat:appcompat:1.6.1")
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
  implementation(libs.androidx.media3.exoplayer)
  implementation(libs.androidx.media3.ui)
  implementation(libs.androidx.media3.exoplayer.dash)
  implementation(libs.androidx.media3.exoplayer.hls)
  implementation("com.google.android.gms:play-services-cast-framework:21.4.0")
  implementation("androidx.mediarouter:mediarouter:1.7.0")
}
