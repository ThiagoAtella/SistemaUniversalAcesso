// build.gradle.kts (Nível do Projeto)

// ESTE ARQUIVO DEVE CONTER APENAS O BLOCO 'plugins'.
// Remova qualquer outro bloco como 'buildscript' ou 'repositories'.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}