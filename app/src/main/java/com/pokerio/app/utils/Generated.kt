package com.pokerio.app.utils

// This annotation allows us to annotate function that are generated at compile time and exclue
// them from code coverage
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Generated
