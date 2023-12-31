package org.example.util;

import com.google.gson.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtil {


    public static JsonElement readJSON(String file) {
        JsonElement json = null;
        try {
            // check if json is valid
            String jsonContent =  new String(Files.readAllBytes(Paths.get(file)));

            try {
                JsonParser.parseString(jsonContent);
            } catch (JsonSyntaxException e) {
                System.err.println("INVALID");
                jsonContent = fixJSON(jsonContent);
            }
            // create JsonElement object
            json = new Gson().fromJson(jsonContent, JsonElement.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static String fixJSON(String jsonContent) {
        String pattern = "\\}[\\r\\n]+";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(jsonContent);

        // Insert commas after closing braces
        StringBuffer sb = new StringBuffer("[\n");
        while (matcher.find()) {
            matcher.appendReplacement(sb, "},");
        }
        matcher.appendTail(sb);
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }
// =====================================================================================================================

    /**
     * @param array: A JsonArray
     * @return
     * isPrimitive : type[0] <br>
     * isNonPrimitive : type[1] <br>
     * isMixed : type[0] && type[1] <br>
     */
    public static boolean[] arrayType(JsonArray array) {
        boolean[] type = new boolean[2];
        for(JsonElement element : array)
            if(element.isJsonPrimitive())
               type[0] = true;
            else
                type[1] = true;
        return type;
    }

// =====================================================================================================================

    public static HashSet<String> invalid = new HashSet<>(Set.of("none", "null", "", " ", "-"));

    public static boolean isInvalidProperty(String prev, String key, String extractedField){

        return  prev == null || key == null ||
                invalid.contains(prev.toLowerCase()) || invalid.contains(key.toLowerCase()) ||
                (prev.equals(key) && extractedField.equals("/"+prev));

    }

    public static boolean isValid(String prev, String key, JsonElement value, String extractedField) {

        return !(isInvalidProperty(prev, key, extractedField) || invalid.contains(value.getAsString().toLowerCase()));
    }


// =====================================================================================================================

    public static Object parseJSONvalue(JsonPrimitive value) {
        if (value == null)
            return null;
        try {
            return value.getAsInt();
        }catch (NumberFormatException e1) {
            try {
                return value.getAsFloat();
            }catch (NumberFormatException e2) {
                try {
                    return value.getAsDouble();
                }catch (NumberFormatException ignored) {
                    if(value.isBoolean())
                        return value.getAsBoolean();
                    else if (value.isString())
                        return value.getAsString();
                }
            }
        }
        return null;
    }

// =====================================================================================================

    public static void saveToJSONFile(String fileName, Object content) {
        File file = new File(fileName);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            String json = gson.toJson(content);
            writer.write(json);
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
