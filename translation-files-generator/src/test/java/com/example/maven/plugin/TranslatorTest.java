package com.example.maven.plugin;

import org.junit.jupiter.api.Test;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.lightningcsv.CsvReader;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

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

        List<Writer> response = translator.translate(reader, StringWriter.class);
        assertEquals(2, response.size());
        assertEquals("" +
                "{\n" +
                "  \"key1\" : \"enval1\",\n" +
                "  \"key2\" : \"enval2\",\n" +
                "  \"key3\" : \"[key3]\"\n" +
                "}", response.get(0).toString());
        assertEquals("" +
                "{\n" +
                "  \"key1\" : \"[key1]\",\n" +
                "  \"key2\" : \"deval2\",\n" +
                "  \"key3\" : \"deval3\"\n" +
                "}", response.get(1).toString());
    }

}
