package com.example.maven.plugin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.simpleflatmapper.csv.CsvParser
import java.io.IOException

internal class TranslatorTest {

    // under test
    var translator = Translator()

    @Test
    @Throws(IOException::class)
    fun translate() {
        val reader = CsvParser.reader(
                """
                "KEY","EN","DE"
                "key1","enval1",
                "key2","enval2","deval2"
                "key3",,"deval3"
                """.trimIndent()
        )
        val response = translator.translate(reader)
        assertEquals(2, response.size)
        assertEquals("" +
                "{\n" +
                "  \"key1\" : \"enval1\",\n" +
                "  \"key2\" : \"enval2\",\n" +
                "  \"key3\" : \"[key3]\"\n" +
                "}", response.get("en"));
        assertEquals("" +
                "{\n" +
                "  \"key1\" : \"[key1]\",\n" +
                "  \"key2\" : \"deval2\",\n" +
                "  \"key3\" : \"deval3\"\n" +
                "}", response.get("de"));
    }

}