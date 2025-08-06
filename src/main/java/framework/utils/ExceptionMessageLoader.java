package framework.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import framework.config.PropertyReader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExceptionMessageLoader {
    private static Map<String, String> exceptionMap = new HashMap<>();

    private ExceptionMessageLoader(){
        throw new UnsupportedOperationException("Exception Message class — do not instantiate.");
    }

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            exceptionMap = mapper.readValue(
                    new File(PropertyReader.readProperty("exceptionJSONPath")),
                    HashMap.class
            );
        } catch (IOException e) {
            System.out.println("Failed to load exception messages: " + e.getMessage());
        }
    }

    public static String getMessageForException(Throwable throwable) {
        String exceptionName = throwable.getClass().getSimpleName();
        return exceptionMap.getOrDefault(exceptionName, "An unexpected error occurred.");
    }
}

