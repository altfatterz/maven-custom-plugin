package com.example.maven.plugin;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.lightningcsv.CsvReader;
import org.simpleflatmapper.util.CloseableIterator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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

    public void execute()  {
        System.out.println("input:" + input.getAbsolutePath());
        System.out.println("output:" + output.getAbsolutePath());

        try (CloseableIterator<String[]> it = CsvParser.iterator(input)) {
            while(it.hasNext()) {
                System.out.println(Arrays.toString(it.next()));
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
