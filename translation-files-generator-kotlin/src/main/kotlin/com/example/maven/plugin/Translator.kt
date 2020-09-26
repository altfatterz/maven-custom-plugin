package com.example.maven.plugin

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import org.simpleflatmapper.lightningcsv.CsvReader
import java.io.IOException
import java.io.StringWriter
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class Translator {

    fun translate(csvReader: CsvReader): Map<String, String> {
        val content = getContent(csvReader)
        val languages = getLanguages(content)
        val response: MutableMap<String, String> = HashMap()
        val jsonFactory = JsonFactory()
        languages.forEach { language: Language ->
            val writer = StringWriter()
            try {
                val jsonGenerator = jsonFactory.createGenerator(writer).setPrettyPrinter(DefaultPrettyPrinter())
                writeContent(jsonGenerator, content, language.column)
                jsonGenerator.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            response[language.code] = writer.toString()
        }
        return response
    }

    @Throws(IOException::class)
    fun writeContent(jsonGenerator: JsonGenerator, content: List<Array<String>>, column: Int) {
        jsonGenerator.writeStartObject()
        content.stream().skip(1).forEach { values ->
            try {
                jsonGenerator.writeFieldName(values[0])
                if (values[column].isEmpty()) {
                    jsonGenerator.writeString("[" + values[0] + "]")
                } else {
                    jsonGenerator.writeString(values[column])
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        jsonGenerator.writeEndObject()
    }

    private fun getContent(csvReader: CsvReader): List<Array<String>> {
        val content: MutableList<Array<String>> = ArrayList()
        val iterator: Iterator<Array<String>> = csvReader.stream().iterator()
        while (iterator.hasNext()) {
            content.add(iterator.next())
        }
        return content
    }

    private fun getLanguages(content: List<Array<String>>): List<Language> {
        val languages: MutableList<Language> = ArrayList()
        val headers = content[0]
        for (i in 1 until headers.size) { // skip the name 'KEY'
            val language = Language(headers[i].toLowerCase(), i)
            languages.add(language)
        }
        return languages
    }

    internal class Language(val code: String, val column: Int)

}