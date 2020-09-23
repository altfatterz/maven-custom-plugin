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

            CloseableCsvReader reader = CsvParser.reader(input);
            JsonFactory jsonFactory = new JsonFactory();

            Iterator<String[]> iterator = reader.iterator();
            String[] headers = iterator.next();

            try (JsonGenerator jgEN = createJsonGenerator("en", jsonFactory);
                 JsonGenerator jgDE = createJsonGenerator("de", jsonFactory);
                 JsonGenerator jgFR = createJsonGenerator("fr", jsonFactory);
                 JsonGenerator jgIT = createJsonGenerator("it", jsonFactory)) {

                writeContent(jgEN, iterator, 2);
                writeContent(jgDE, iterator, 3);
                writeContent(jgFR, iterator, 4);
                writeContent(jgIT, iterator, 5);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeContent(JsonGenerator jsonGenerator, Iterator<String[]> iterator, int column) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeRaw('\n');
        while (iterator.hasNext()) {
            String[] values = iterator.next();
            getLog().info("values:");
            for (String value : values) {
                getLog().info(value);
            }
            jsonGenerator.writeFieldName(values[0]);
            jsonGenerator.writeString(values[column]);
        }
        jsonGenerator.writeRaw('\n');
        jsonGenerator.writeEndObject();
    }

    Path createTranslationFile(String language) throws IOException {
        Path path = Paths.get(output + "/locale-" + language + ".json");
        System.out.println("path:" + path);
        // Files.deleteIfExists(path);
        return Files.createFile(path);
    }

    JsonGenerator createJsonGenerator(String language, JsonFactory jsonFactory) throws IOException {
        return jsonFactory.createGenerator(Files.newOutputStream(createTranslationFile(language)))
                .setPrettyPrinter(new DefaultPrettyPrinter());
    }

}
