plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.gayatri.gayatripdfviewlibrary"
    compileSdk = 34

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Enable publishing for specific variant
    publishing {
        singleVariant("release")
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            // Use the correct variant to include the AAR file
            afterEvaluate {
                from(components["release"])
            }

            groupId = "com.gayatri"
            artifactId = "gayatripdfviewlibrary"
            version = "1.0.0"

            pom {
                name.set("Gayatri PDF View Library")
                description.set("A lightweight library for viewing and downloading PDFs in Android.")
                url.set("https://github.com/GayatriJoshi96/gayatripdfviewlibrary")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("gayatrijoshi")
                        name.set("Gayatri Joshi")
                        email.set("gayatrij306@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/GayatriJoshi96/gayatripdfviewlibrary.git")
                    developerConnection.set("scm:git:ssh://git@github.com:GayatriJoshi96/gayatripdfviewlibrary.git")
                    url.set("https://github.com/GayatriJoshi96/gayatripdfviewlibrary")
                }
            }
        }
    }

    repositories {
        maven {
            url = uri(project.layout.buildDirectory.dir("repos").get().asFile)
        }
    }
}
