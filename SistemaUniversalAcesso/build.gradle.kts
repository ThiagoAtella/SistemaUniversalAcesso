// build.gradle.kts (Nível do Projeto)

// ESTE ARQUIVO DEVE CONTER APENAS O BLOCO 'plugins'.
// Remova qualquer outro bloco como 'buildscript' ou 'repositories'.
plugins {
    alias(libs.plugins.android.application) apply false
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.1" apply false
}