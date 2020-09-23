package com.example.maven.plugin;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.lightningcsv.CloseableCsvReader;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Mojo(name = "dependency-counter", defaultPhase = LifecyclePhase.COMPILE)
public class DependencyCounterMojo extends AbstractMojo {

    /**
     * Scope to filter the dependencies.
     */
    @Parameter(property = "scope")
    String scope;

    @Parameter(defaultValue = "src/main/resources/i18n/translations.csv", required = true)
    private File input;

    @Parameter(defaultValue = "public/i18n", required = true)
    private File output;

    /**
     * Gives access to the Maven project information.
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    public void execute() {
        System.out.println("input:" + input.getAbsolutePath());
        System.out.println("output:" + output.getAbsolutePath());

        try {
            CloseableCsvReader reader = CsvParser.reader(input);
            JsonFactory jsonFactory = new JsonFactory();

            Iterator<String[]> iterator = reader.iterator();
            String[] headers = iterator.next();

            try (JsonGenerator jsonGenerator = jsonFactory.createGenerator(System.out)) {
                jsonGenerator.writeStartObject();
                while (iterator.hasNext()) {
                    String[] values = iterator.next();
                    jsonGenerator.writeFieldName(values[0]);
                    jsonGenerator.writeString(values[1]);
                }
                jsonGenerator.writeEndObject();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Dependency> dependencies = project.getDependencies();

        long numDependencies = dependencies.stream()
                .filter(d -> (scope == null || scope.isEmpty()) || scope.equals(d.getScope()))
                .count();

        getLog().info("Number of dependencies: " + numDependencies);
    }

}
