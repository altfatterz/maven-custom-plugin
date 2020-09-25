package com.example.maven.plugin;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import org.simpleflatmapper.lightningcsv.CloseableCsvReader;
import org.simpleflatmapper.lightningcsv.CsvReader;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Translator {

    public List<Map<String, String>> translate(CsvReader csvReader) {
        List<String[]> content = getContent(csvReader);
        List<Language> languages = getLanguages(content);

        List<Map<String, String>> response = new ArrayList<>();
        JsonFactory jsonFactory = new JsonFactory();

        languages.forEach(language -> {
            Writer writer = new StringWriter();
            try {
                JsonGenerator jsonGenerator = jsonFactory.createGenerator(writer).setPrettyPrinter(new DefaultPrettyPrinter());
                writeContent(jsonGenerator, content, language.column);
                jsonGenerator.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.add(Collections.singletonMap(language.code, writer.toString()));
        });

        return response;
    }

    void writeContent(JsonGenerator jsonGenerator, List<String[]> content, int column) throws IOException {
        jsonGenerator.writeStartObject();
        content.stream().skip(1).forEach(values -> {
            try {
                jsonGenerator.writeFieldName(values[0]);
                if (values[column] != null && values[column].isEmpty()) {
                    jsonGenerator.writeString("[" + values[0] + "]");
                } else {
                    jsonGenerator.writeString(values[column]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        jsonGenerator.writeEndObject();
    }

    private List<String[]> getContent(CsvReader csvReader) {
        List<String[]> content = new ArrayList<>();
        Iterator<String[]> iterator = csvReader.stream().iterator();
        while (iterator.hasNext()) {
            content.add(iterator.next());
        }
        return content;
    }

    private List<Language> getLanguages(List<String[]> content) {
        List<Language> languages = new ArrayList<>();
        String[] headers = content.get(0);
        for (int i = 1; i < headers.length; i++) {
            Language language = new Language(headers[i].toLowerCase(), i);
            languages.add(language);
        }
        return languages;
    }

    static class Language {

        String code;
        int column;

        public Language(String code, int column) {
            this.code = code;
            this.column = column;
        }
    }
}
