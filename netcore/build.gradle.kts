plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.lokal.netcore"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)

    implementation(libs.gson)
    implementation(libs.gson.converter)
    implementation(libs.retrofit)
    implementation(libs.firebase.crashlytics)

    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-compiler:2.48.1")

}

val token: String? = project.findProperty("gpr.token") as String?
publishing {
    publications {
        create<MavenPublication>("release") {
            run {
                groupId = "com.lokal"
                artifactId = "netcore"
                version = "1.0.0"
                artifact("$buildDir/outputs/aar/netcore-release.aar")
            }
        }
    }

    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/pranavkapoor001/Netcore")
            credentials {

                // Credentials are stored locally in settings.gradle
                username = project.findProperty("gpr.user").toString()
                password = token // TODO: Replace with token for release publish
            }
        }
    }
}