plugins {
    alias(libs.plugins.openapi.generator)
}


openApiGenerate {
    generatorName.set("dart-dio")

    inputSpec = "$rootDir/abacusflow-portal/abacusflow-portal-web/src/main/resources/static/openapi.yaml"
    outputDir =
        layout.buildDirectory
            .dir("generated/openapi")
            .get()
            .asFile.absolutePath

    configOptions.set(
        mapOf(
            "pubName" to "abacusflow_openapi",
            "serializationLibrary" to "json_serializable",
            "dateLibrary" to "core"
        ),
    )
    cleanupOutput.set(true)
}