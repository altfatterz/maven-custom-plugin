package com.example.maven.plugin;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.lightningcsv.CloseableCsvReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

@Mojo(name = "dependency-counter", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class DependencyCounterMojo extends AbstractMojo {

    @Parameter(defaultValue = "src/main/resources/static/i18n/translations.csv", required = true)
    private File input;

    @Parameter(defaultValue = "target/classes/static/i18n", required = true)
    private File output;

    public void execute() {
        getLog().info("input:" + input.getAbsolutePath());
        getLog().info("output:" + output.getAbsolutePath());
        getLog().info("Generating translation files...");

        try {
            generateTranslationInJson(input, "en", 1);
            generateTranslationInJson(input, "de", 2);
            generateTranslationInJson(input, "fr", 3);
            generateTranslationInJson(input, "it", 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateTranslationInJson(File input, String language, int languageColumn) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        CloseableCsvReader reader = CsvParser.reader(input);
        Iterator<String[]> iterator = reader.iterator();
        iterator.next(); // ignore headers
        try (JsonGenerator jsonGenerator = createJsonGenerator(language, jsonFactory)) {
            writeContent(jsonGenerator, iterator, languageColumn);
        }
    }


    void writeContent(JsonGenerator jsonGenerator, Iterator<String[]> iterator, int column) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeRaw('\n');
        while (iterator.hasNext()) {
            String[] values = iterator.next();
            jsonGenerator.writeFieldName(values[0]);
            if (values[column] != null && values[column].isEmpty()) {
                jsonGenerator.writeString(values[0]);
            } else {
                jsonGenerator.writeString(values[column]);
            }

        }
        jsonGenerator.writeRaw('\n');
        jsonGenerator.writeEndObject();
    }

    Path createTranslationFile(String language) throws IOException {
        Path path = Paths.get(output + "/locale-" + language + ".json");
        Files.deleteIfExists(path);
        return Files.createFile(path);
    }

    JsonGenerator createJsonGenerator(String language, JsonFactory jsonFactory) throws IOException {
        return jsonFactory.createGenerator(Files.newOutputStream(createTranslationFile(language)))
                .setPrettyPrinter(new DefaultPrettyPrinter());
    }

}
