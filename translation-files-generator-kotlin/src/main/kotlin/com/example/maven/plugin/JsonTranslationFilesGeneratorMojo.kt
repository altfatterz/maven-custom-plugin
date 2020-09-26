package com.example.maven.plugin

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.simpleflatmapper.csv.CsvParser
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
class JsonTranslationFilesGeneratorMojo : AbstractMojo() {

    @Parameter(defaultValue = "src/main/resources/static/i18n/translations.csv", required = true)
    private lateinit var input: File

    @Parameter(defaultValue = "target/classes/static/i18n", required = true)
    private lateinit var output: File

    override fun execute() {
        log.info("Generating JSON translation files...")
        log.info("Processing input CSV translation file from :" + input.absolutePath)
        log.info("Output will be written to directory:" + output.absolutePath)
        val translator = Translator()
        try {
            val response = translator.translate(CsvParser.reader(FileReader(input)))
            response.keys.stream().forEach { language ->
                try {
                    val translationFile = createTranslationFile(language)
                    Files.writeString(translationFile, response[language], StandardCharsets.UTF_8)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        log.info("JSON translation were successfully generated")
    }

    @Throws(IOException::class)
    fun createTranslationFile(language: String): Path {
        val path = Paths.get(output.toString() + "/locale-" + language + ".json")
        Files.deleteIfExists(path)
        return Files.createFile(path)
    }
}
