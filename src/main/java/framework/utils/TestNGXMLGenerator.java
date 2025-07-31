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

    static class TestCaseData {
        String suiteName;
        String testName;
        String className;
        String testCaseID;
        String methodName;
        String[] paramName;
        String[] paramValue;
        String executeFlag;

        public TestCaseData(String suiteName, String testName, String className,String testCaseID, String methodName,
                            String[] paramName, String[] paramValue, String executeFlag) {
            this.suiteName = suiteName;
            this.testName = testName;
            this.className = className;
            this.testCaseID = testCaseID;
            this.methodName = methodName;
            this.paramName = paramName;
            this.paramValue = paramValue;
            this.executeFlag = executeFlag;
        }
    }

    public static void main(String[] args) throws Exception {
        String outputPath = "generated.xml";
        String path = PropertyReader.readProperty("testNGPath");
        List<TestCaseData> testData = readExcel(path);
        XmlSuite suite = buildSuite(testData);
        TestNGXMLGenerator.writeSuiteToXmlFile(suite, outputPath);
        runSuite(suite);
    }

    static List<TestCaseData> readExcel(String path) throws Exception {
        List<TestCaseData> data = new ArrayList<>();
        FileInputStream fis = new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet;
        sheet = workbook.getSheet(appModule);


        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            data.add(new TestCaseData(
                    row.getCell(0).getStringCellValue(),
                    row.getCell(1).getStringCellValue(),
                    row.getCell(2).getStringCellValue(),
                    row.getCell(3).getStringCellValue(),
                    row.getCell(4).getStringCellValue(),
                    row.getCell(5).getStringCellValue().split(","),
                    row.getCell(6).getStringCellValue().split(","),
                    row.getCell(7).getStringCellValue()
            ));
        }
        workbook.close();
        fis.close();
        return data;
    }

    static XmlSuite buildSuite(List<TestCaseData> data) {
        XmlSuite suite = new XmlSuite();
        suite.setName(appModule + " " + suiteType + " " + "Suite");
        suite.setParallel(XmlSuite.ParallelMode.TESTS);
        suite.setThreadCount(4);
        suite.addListener("framework.listeners.DynamicDependencyTransformer");

        Map<String, Map<String, List<String>>> testMap = new LinkedHashMap<>();
        Map<String, String> testParams = new LinkedHashMap<>();

        for (TestCaseData row : data) {
            if (!"Y".equalsIgnoreCase(row.executeFlag) || !row.suiteName.contains(suiteType)) continue;

            testMap
                    .computeIfAbsent(row.testName, k -> new LinkedHashMap<>())
                    .computeIfAbsent(row.className, k -> new ArrayList<>())
                    .add(row.methodName);
            for(int i=0; i<row.paramName.length;i++)
                    testParams.put(row.paramName[i], row.paramValue[i]);
        }

        suite.setParameters(testParams);

        for (String testName : testMap.keySet()) {
            XmlTest xmlTest = new XmlTest(suite);
            xmlTest.setName(testName);
            xmlTest.setParallel(XmlSuite.ParallelMode.NONE);
            xmlTest.setThreadCount(-1);

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