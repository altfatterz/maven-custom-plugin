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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Mojo(name = "dependency-counter", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class DependencyCounterMojo extends AbstractMojo {

    @Parameter(defaultValue = "src/main/resources/static/i18n/translations.csv", required = true)
    private File input;

    @Parameter(defaultValue = "target/classes/static/i18n", required = true)
    private File output;

    public void execute() {
        getLog().info("Generating JSON translation files...");
        getLog().info("Processing input CSV translation file from :" + input.getAbsolutePath());
        getLog().info("Output will be written to directory:" + output.getAbsolutePath());

        List<Language> languages = getLanguages(input);
        languages.stream().forEach(language -> generateTranslationInJson(input, language.code, language.column));

        getLog().info("JSON translation were successfully generated");
    }


    private List<Language> getLanguages(File input) {
        List<Language> languages = Collections.emptyList();
        try (CloseableCsvReader reader = CsvParser.reader(input)) {
            String[] headers = reader.iterator().next();
            languages = IntStream.range(1, headers.length)
                    .mapToObj(i -> new Language(headers[i].toLowerCase(), i))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return languages;
    }

    public void generateTranslationInJson(File input, String language, int languageColumn) {
        try (CloseableCsvReader reader = CsvParser.reader(input)) {
            Iterator<String[]> iterator = reader.iterator();
            iterator.next(); // ignore headers
            try (JsonGenerator jsonGenerator = createJsonGenerator(language, new JsonFactory())) {
                writeContent(jsonGenerator, iterator, languageColumn);
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
            jsonGenerator.writeFieldName(values[0]);
            if (values[column] != null && values[column].isEmpty()) {
                jsonGenerator.writeString("[" + values[0] + "]");
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

    static class Language {

        String code;
        int column;

        public Language(String code, int column) {
            this.code = code;
            this.column = column;
        }

        public String getCode() {
            return code;
        }

        public int getColumn() {
            return column;
        }
    }
}
