package framework.utils;

import framework.config.PropertyReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.TestNG;
import org.testng.xml.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
public class TestNGXMLGenerator {

    private static final Logger log = LoggerFactory.getLogger(TestNGXMLGenerator.class);
    static String appName = AppConfigUtil.getAppName();
    static String[] suiteArray = appName.split("_");
    static String appModule = suiteArray[0];
    static String suiteType = suiteArray[1];
    private final String suiteName;
    private final String testName;
    private final String className;
    private final String testCaseID;
    private final String methodName;
    private final String[] paramName;
    private final String[] paramValue;
    private final String executeFlag;
    public static String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    public static String resultsDir = "allure-results-" + timestamp;

    private TestNGXMLGenerator(Builder builder){
        suiteName = builder.suiteName;
        testName = builder.testName;
        className = builder.className;
        testCaseID = builder.testCaseID;
        methodName = builder.methodName;
        paramName = builder.paramName;
        paramValue = builder.paramValue;
        executeFlag = builder.executeFlag;
    }

    public static class Builder {
        private String suiteName;
        private String testName;
        private String className;
        private String testCaseID;
        private String methodName;
        private String[] paramName;
        private String[] paramValue;
        private String executeFlag;

        public Builder setSuiteName(String suiteName){
            this.suiteName = suiteName;
            return this;
        }

        public Builder setTestName(String testName){
            this.testName = testName;
            return this;
        }

        public Builder setClassName(String className){
            this.className = className;
            return this;
        }

        public Builder setTestCaseID(String testCaseID){
            this.testCaseID = testCaseID;
            return this;
        }

        public Builder setMethodName(String methodName){
            this.methodName = methodName;
            return this;
        }

        public Builder setParamName(String[] paramName){
            this.paramName = paramName;
            return this;
        }

        public Builder setParamValue(String[] paramValue){
            this.paramValue = paramValue;
            return this;
        }

        public Builder setExecuteFlag(String executeFlag){
            this.executeFlag = executeFlag;
            return this;
        }

        public TestNGXMLGenerator build() {
            return new TestNGXMLGenerator(this);
        }

    }

    public static void main(String[] args) throws Exception {
        String outputPath = "generated.xml";
        String path = PropertyReader.readProperty("testNGPath");
        List<TestNGXMLGenerator> testData = readExcel(path);
        System.setProperty("allure.results.directory", resultsDir);
        XmlSuite suite = buildSuite(testData);
        TestNGXMLGenerator.writeSuiteToXmlFile(suite, outputPath);
        runSuite(suite);
    }

    static List<TestNGXMLGenerator> readExcel(String path) throws Exception {
        List<TestNGXMLGenerator> data = new ArrayList<>();
        try(FileInputStream fis = new FileInputStream(path)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet;
            sheet = workbook.getSheet(appModule);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                data.add(new TestNGXMLGenerator.Builder()
                        .setSuiteName(row.getCell(0).getStringCellValue())
                        .setTestName(row.getCell(1).getStringCellValue())
                        .setClassName(row.getCell(2).getStringCellValue())
                        .setTestCaseID(row.getCell(3).getStringCellValue())
                        .setMethodName(row.getCell(4).getStringCellValue())
                        .setParamName(row.getCell(5).getStringCellValue().split(","))
                        .setParamValue(row.getCell(6).getStringCellValue().split(","))
                        .setExecuteFlag(row.getCell(7).getStringCellValue()).build());
            }
            workbook.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    static XmlSuite buildSuite(List<TestNGXMLGenerator> data) {
        XmlSuite suite = new XmlSuite();
        suite.setName(appModule + " " + suiteType + " " + "Suite");
        suite.addListener("framework.listeners.CustomTestListener");
        suite.addListener("framework.listeners.DynamicDependencyTransformer");
        suite.addListener("io.qameta.allure.testng.AllureTestNg");

        Map<String, Map<String, List<String>>> testMap = new LinkedHashMap<>();
        Map<String,Map<String, String>> testParams = new LinkedHashMap<>();


        for (TestNGXMLGenerator row : data) {
            if (!"Y".equalsIgnoreCase(row.executeFlag) || !row.suiteName.contains(suiteType)) continue;

            testMap
                    .computeIfAbsent(row.testName, k -> new LinkedHashMap<>())
                    .computeIfAbsent(row.className, k -> new ArrayList<>())
                    .add(row.methodName);
            Map<String,String> params = new LinkedHashMap<>();
            for(int i=0; i<row.paramName.length;i++) {
                params.put(row.paramName[i], row.paramValue[i]);
            }
            testParams.put(row.testName,params);
        }

        Map<String, String> suiteParams = new HashMap<>();
        for (Map<String, String> paramMap : testParams.values()) {
            if (paramMap.containsKey("env")) {
                suiteParams.put("env", paramMap.get("env"));
                break;
            }
        }
        suite.setParameters(suiteParams);

        if(PropertyReader.readProperty("parallelFlag").equalsIgnoreCase("true")) {
            suite.setParallel(XmlSuite.ParallelMode.TESTS);
            if(testMap.size() > 5) {
                suite.setThreadCount(5);
            } else {
                suite.setThreadCount(testMap.size());
            }
        }else if(PropertyReader.readProperty("parallelFlag").equalsIgnoreCase("false")){
            suite.setThreadCount(1);
        }

        for (String testName : testMap.keySet()) {
            XmlTest xmlTest = new XmlTest(suite);
            xmlTest.setName(testName);
            Map<String,String> parameter = testParams.get(testName);
            xmlTest.setParameters(parameter);
            List<XmlClass> xmlClasses = new ArrayList<>();
            for (Map.Entry<String, List<String>> classEntry : testMap.get(testName).entrySet()) {
                XmlClass xmlClass = new XmlClass(classEntry.getKey());
                List<XmlInclude> includes = new ArrayList<>();
                for (String method : classEntry.getValue()) {
                    includes.add(new XmlInclude(method));
                }
                xmlClass.setIncludedMethods(includes);
                xmlClasses.add(xmlClass);
            }

            xmlTest.setXmlClasses(xmlClasses);
        }
        return suite;
    }

    static void runSuite(XmlSuite suite) {
        TestNG testng = new TestNG();
        testng.setXmlSuites(Collections.singletonList(suite));
        testng.run();
    }

    public static void writeSuiteToXmlFile(XmlSuite suite, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            String xmlContent = suite.toXml();
            writer.write(xmlContent);
            System.out.println("Suite XML written to: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to write XML file: " + e.getMessage());
        }
    }
}