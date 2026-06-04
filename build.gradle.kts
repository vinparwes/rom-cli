// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "9.1.1" apply false
    id("org.jetbrains.kotlin.android") version "2.3.21" apply false
    id("com.google.devtools.ksp") version "2.3.9" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.9.8" apply false
    id("de.undercouch.download") version "5.7.0" apply false
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        val nav_version = "2.9.8"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }
}