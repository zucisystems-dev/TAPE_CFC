package framework.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.ITestContext;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataProvider {

    public static String getJsonPath(String browser, Class<?> clazz) {
        String className = clazz.getSimpleName();

        if (browser.contains("chrome") || browser.contains("safari") || browser.equalsIgnoreCase("edge")) {
            browser = "web";
        } else if (browser.contains("ios") || browser.contains("android")) {
            browser = "mobile";
        } else {
            browser = "api";
        }

        return System.getProperty("user.dir") + "/src/test/java/testData/" + browser + "/" + className + ".json";
    }

    @org.testng.annotations.DataProvider(name = "commonDataProvider", parallel = true)
    public Object[][] getTestData(Method method, ITestContext context)  {
        String groupName = context.getCurrentXmlTest().getParameter("env");
        String browser = context.getCurrentXmlTest().getParameter("browser");
        String scriptId = method.getName();
        Class<?> testClass = method.getDeclaringClass();

        String path = getJsonPath(browser,testClass);

        ObjectMapper mapper = new ObjectMapper();
        InputStream is = null;
        try {
            is = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Map<String, List<Map<String, Object>>> fullData =
                null;
        try {
            fullData = mapper.readValue(is, new TypeReference<Map<String, List<Map<String, Object>>>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Map<String, Object>> rawList = fullData.getOrDefault(groupName, Collections.emptyList());

        List<Map<String, Object>> matchedList = rawList.stream()
                .filter(item -> scriptId.equals(item.get("automationId")))
                .collect(Collectors.toList());

        List<Map<String, String>> finalList = new ArrayList<>();
        for (Map<String, Object> item : matchedList) {
            Map<String, String> converted = item.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue() == null ? null : e.getValue().toString()
                    ));
            finalList.add(converted);
        }

        if (finalList.isEmpty()) {
            throw new RuntimeException("No test data found for method: " + scriptId + " in env: " + groupName);
        }

        Object[][] result = new Object[finalList.size()][1];
        for (int i = 0; i < finalList.size(); i++) {
            result[i][0] = finalList.get(i);
        }

        return result;
    }

}
