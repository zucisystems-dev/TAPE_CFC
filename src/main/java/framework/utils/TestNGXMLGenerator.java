package framework.utils;

import framework.config.PropertyReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.TestNG;
import org.testng.xml.*;

import java.io.*;
import java.util.*;
public class TestNGXMLGenerator {

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
    private final String parallelFlag;

    private TestNGXMLGenerator(Builder builder){
        suiteName = builder.suiteName;
        testName = builder.testName;
        className = builder.className;
        testCaseID = builder.testCaseID;
        methodName = builder.methodName;
        paramName = builder.paramName;
        paramValue = builder.paramValue;
        executeFlag = builder.executeFlag;
        parallelFlag = builder.parallelFlag;
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
        private String parallelFlag;

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

        public Builder setParallelFlag(String parallelFlag){
            this.parallelFlag = parallelFlag;
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
                        .setExecuteFlag(row.getCell(7).getStringCellValue())
                        .setParallelFlag(row.getCell(8).getStringCellValue()).build());
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
        suite.setParallel(XmlSuite.ParallelMode.TESTS);
        suite.addListener("framework.listeners.DynamicDependencyTransformer");
        suite.addListener("framework.listeners.CustomTestListener");

        Map<String, Map<String, List<String>>> testMap = new LinkedHashMap<>();
        Map<String, String> testParams = new LinkedHashMap<>();

        for (TestNGXMLGenerator row : data) {
            if (!"Y".equalsIgnoreCase(row.executeFlag) || !row.suiteName.contains(suiteType)) continue;

            testMap
                    .computeIfAbsent(row.testName, k -> new LinkedHashMap<>())
                    .computeIfAbsent(row.className, k -> new ArrayList<>())
                    .add(row.methodName);
            for(int i=0; i<row.paramName.length;i++)
                    testParams.put(row.paramName[i], row.paramValue[i]);
        }

        suite.setParameters(testParams);
        if(testMap.size()>5){
            suite.setThreadCount(5);
        }else{
            suite.setThreadCount(testMap.size());
        }

        for (String testName : testMap.keySet()) {
            XmlTest xmlTest = new XmlTest(suite);
            xmlTest.setName(testName);
            for(TestNGXMLGenerator row : data) {
                if(testName.equalsIgnoreCase(row.testName) && row.parallelFlag.equalsIgnoreCase("N")) {
                    xmlTest.setParallel(XmlSuite.ParallelMode.NONE);
                    break;
                }
            }
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