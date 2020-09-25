package com.example.maven.plugin;

import org.junit.jupiter.api.Test;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.lightningcsv.CsvReader;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TranslatorTest {

    // under test
    Translator translator = new Translator();

    @Test
    public void translate() throws IOException {
        CsvReader reader = CsvParser.reader(
                "\"KEY\",\"EN\",\"DE\"\n" +
                        "\"key1\",\"enval1\",\n" +
                        "\"key2\",\"enval2\",\"deval2\"\n" +
                        "\"key3\",,\"deval3\""
        );

        List<Map<String, String>> response = translator.translate(reader);
        assertEquals(2, response.size());
        assertEquals("" +
                "{\n" +
                "  \"key1\" : \"enval1\",\n" +
                "  \"key2\" : \"enval2\",\n" +
                "  \"key3\" : \"[key3]\"\n" +
                "}", response.get(0).get("en"));
        assertEquals("" +
                "{\n" +
                "  \"key1\" : \"[key1]\",\n" +
                "  \"key2\" : \"deval2\",\n" +
                "  \"key3\" : \"deval3\"\n" +
                "}", response.get(1).get("de"));
    }

}
