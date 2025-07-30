package framework.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.ITestContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataProvider {

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

    @org.testng.annotations.DataProvider(name = "ecsDataProvider", parallel = true)
    public Object[][] getTestData(Method method, ITestContext context) throws Exception {
        String groupName = context.getCurrentXmlTest().getParameter("env");
        String browser = context.getCurrentXmlTest().getParameter("browser");
        String scriptId = method.getName();
        Class<?> testClass = method.getDeclaringClass();

        String path = getJsonPath(browser,testClass);

        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new FileInputStream(new File(path));

        Map<String, List<Map<String, Object>>> fullData =
                mapper.readValue(is, new TypeReference<Map<String, List<Map<String, Object>>>>() {});

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

        Object[][] result = new Object[finalList.size()][1];
        for (int i = 0; i < finalList.size(); i++) {
            result[i][0] = finalList.get(i);
        }

        return result;
    }

}
