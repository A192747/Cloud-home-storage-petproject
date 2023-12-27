package org.example.utils;
import org.example.Main;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JsonWorker {
    private static JSONArray array;

    private JsonWorker() {}
    public static void init() throws IOException, ParseException {
        String path = Main.properties.getProperty("location_keyboard_json");
        JSONParser parser = new JSONParser();
        array = (JSONArray) parser.parse(getJsonFile(path));
    }
    private static String getJsonFile(String FilePath) throws IOException {
        StringBuilder builder = new StringBuilder();
        List<String> lines = Files.readAllLines(Paths.get(FilePath));
        lines.forEach(builder::append);
        if(builder.length() == 0)
            return "[]";
        return builder.toString();
    }

    public static JSONArray getArray(){
        return array;
    }
}
