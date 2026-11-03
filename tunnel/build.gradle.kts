@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestLogEvent

val pkg: String = providers.gradleProperty("amneziawgPackageName").get()
// Package name of the app that embeds this library. amneziawg-go puts its UAPI
// socket under /data/data/<package>/cache/amneziawg (non-fatal if wrong).
val cmakeAndroidPackageName: String = providers.environmentVariable("ANDROID_PACKAGE_NAME")
    .orElse(providers.gradleProperty("awgHostAppPackageName"))
    .getOrElse(pkg)

plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    namespace = "${pkg}.tunnel"
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
    externalNativeBuild {
        cmake {
            path("tools/CMakeLists.txt")
        }
    }
    testOptions.unitTests.all {
        it.testLogging { events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED) }
    }
    buildTypes {
        all {
            externalNativeBuild {
                cmake {
                    targets("libawg-go.so")
                    arguments("-DGRADLE_USER_HOME=${project.gradle.gradleUserHomeDir}")
                }
            }
        }
        release {
            externalNativeBuild {
                cmake {
                    arguments("-DANDROID_PACKAGE_NAME=${cmakeAndroidPackageName}")
                }
            }
        }
        debug {
            externalNativeBuild {
                cmake {
                    arguments("-DANDROID_PACKAGE_NAME=${cmakeAndroidPackageName}.debug")
                }
            }
        }
    }
    lint {
        disable += "LongLogTag"
        disable += "NewApi"
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.collection)
    compileOnly(libs.jsr305)
    testImplementation(libs.junit)
}

// JitPack: coordinates com.github.hung55555hung:amneziawg-android:<tag>
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = providers.gradleProperty("publishGroup").getOrElse("com.github.hung55555hung")
                artifactId = "amneziawg-android"
                version = providers.environmentVariable("VERSION")
                    .orElse(providers.gradleProperty("amneziawgVersionName"))
                    .get()
            }
        }
    }
}
