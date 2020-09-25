package com.example.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.simpleflatmapper.csv.CsvParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class JsonTranslationFilesGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "src/main/resources/static/i18n/translations.csv", required = true)
    private File input;

    @Parameter(defaultValue = "target/classes/static/i18n", required = true)
    private File output;

    public void execute() {
        getLog().info("Generating JSON translation files...");
        getLog().info("Processing input CSV translation file from :" + input.getAbsolutePath());
        getLog().info("Output will be written to directory:" + output.getAbsolutePath());

        Translator translator = new Translator();
        try {
            Map<String, String> response = translator.translate(CsvParser.reader(new FileReader(input)));
            response.keySet().stream().forEach(language -> {
                try {
                    Path translationFile = createTranslationFile(language);
                    Files.writeString(translationFile, response.get(language), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        getLog().info("JSON translation were successfully generated");
    }

    Path createTranslationFile(String language) throws IOException {
        Path path = Paths.get(output + "/locale-" + language + ".json");
        Files.deleteIfExists(path);
        return Files.createFile(path);
    }

}
