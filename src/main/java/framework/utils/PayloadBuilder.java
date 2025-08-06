package framework.utils;

import com.google.gson.*;
import framework.config.PropertyReader;
import org.testng.ITestContext;

import java.lang.reflect.Method;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static framework.base.TestBase.getTestData;

public class PayloadBuilder {

    private static final String PAYLOAD_FILE = PropertyReader.readProperty("payloadsPath");

    private PayloadBuilder(){
        throw new UnsupportedOperationException("Payload Builder class — do not instantiate.");
    }

    public static String getJsonPath(String browser, Class<?> clazz) {
        String className = clazz.getSimpleName();

        if (browser.equalsIgnoreCase("chrome")) {
            browser = "web";
        } else if (browser.equalsIgnoreCase("ios") || browser.equalsIgnoreCase("android")) {
            browser = "mobile";
        } else {
            browser = "api";
        }

        return System.getProperty("user.dir") + "/src/test/java/testData/" + browser + "/" + className + ".json";
    }

    private static JsonObject loadJsonFile(String path) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(path)));
        return JsonParser.parseString(content).getAsJsonObject();
    }

    public static JsonObject buildJsonPayload() {
        JsonObject payloads;
        try {
            payloads = loadJsonFile(PAYLOAD_FILE);
        } catch (Exception e) {
            throw new RuntimeException("Error loading payload file", e);
        }

        JsonObject templateObj = payloads.getAsJsonObject(getTestData().get("payloadKey")).getAsJsonObject("payload");
        JsonObject clonedTemplate = JsonParser.parseString(templateObj.toString()).getAsJsonObject();

        JsonObject finalPayload = replacePlaceholders(clonedTemplate, getTestData()).getAsJsonObject();

        return finalPayload;
    }

    public static JsonElement replacePlaceholders(JsonElement element, Map<String, String> replacements) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            JsonObject updated = new JsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                updated.add(entry.getKey(), replacePlaceholders(entry.getValue(), replacements));
            }
            return updated;
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            JsonArray updated = new JsonArray();
            for (JsonElement item : array) {
                updated.add(replacePlaceholders(item, replacements));
            }
            return updated;
        } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String value = element.getAsString();

            // Find placeholders in the value (e.g. "{{username}}")
            Pattern pattern = Pattern.compile("\\$\\{(.+?)}");
            Matcher matcher = pattern.matcher(value);

            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String key = matcher.group(1);
                if (replacements.containsKey(key)) {
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(replacements.get(key)));
                } else {
                    throw new IllegalArgumentException("Missing replacement value for key: " + key);
                }
            }
            matcher.appendTail(sb);
            return new JsonPrimitive(sb.toString());
        }

        return element; // numbers, booleans, nulls, etc.
    }
}

