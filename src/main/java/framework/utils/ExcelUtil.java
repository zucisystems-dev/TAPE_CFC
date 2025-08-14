package framework.utils;

import com.google.gson.Gson;
import framework.config.PropertyReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

    private ExcelUtil(){
        throw new UnsupportedOperationException("Excel Util class — do not instantiate.");
    }

        public static Map<String, List<String>> getDependencies(String filePath) {
            Map<String, List<String>> dependencies = new HashMap<>();

            try (FileInputStream fis = new FileInputStream(filePath);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    Cell testNameCell = row.getCell(4);
                    Cell dependsOnCell = row.getCell(8);

                    String testName = testNameCell.getStringCellValue();
                    String dependsOn = dependsOnCell.getStringCellValue();

                    if(!dependsOn.equalsIgnoreCase("null")) {
                        List<String> dependsList = Arrays.asList(dependsOn.split(","));
                        dependencies.put(testName, dependsList);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return dependencies;
        }

    public static void writeFromJson(String jsonFilePath, String excelFilePath) {
        try (Reader reader = new FileReader(jsonFilePath)) {

            Type listType = new TypeToken<List<TestExecutionLogger>>() {}.getType();

            List<TestExecutionLogger> testResults = new Gson().fromJson(reader, listType);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Test Results");

            String[] headers = { "Test Name", "Browser", "Status", "Failure Reason", "Environment", "Test Start Time", "Test End Time","Execution Time" };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                sheet.autoSizeColumn(i);
            }

            int rowIndex = 1;
            for (TestExecutionLogger result : testResults) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(result.getTestName());
                row.createCell(1).setCellValue(result.getBrowser());
                row.createCell(2).setCellValue(result.getStatus());
                row.createCell(3).setCellValue(result.getFailureReason() == null ? "Test Passed or Skipped" : result.getFailureReason());
                row.createCell(4).setCellValue(result.getEnvironment());
                row.createCell(5).setCellValue(result.getTestStartTime());
                row.createCell(6).setCellValue(result.getTestEndTime());
                row.createCell(7).setCellValue(result.getTestExecutionTime());
            }

            try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
                workbook.write(fileOut);
            }

            workbook.close();
            System.out.println("Excel report written to: " + excelFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFromList(List<TestExecutionLogger> results) {
        try{
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Test Results");

            CellStyle headerStyle = getHeaderCellStyle(workbook);

            String[] headers = { "Test Name", "Browser", "Status", "Failure Reason", "Environment", "Test Start Time", "Test End Time","Execution Time", "Execution Type", "Thread Count"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (TestExecutionLogger result : results) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(result.getTestName());
                row.createCell(1).setCellValue(result.getBrowser());
                row.createCell(2).setCellValue(result.getStatus());
                row.createCell(3).setCellValue(result.getFailureReason() == null ? "Test Passed or Skipped" : result.getFailureReason());
                row.createCell(4).setCellValue(result.getEnvironment());
                row.createCell(5).setCellValue(result.getTestStartTime());
                row.createCell(6).setCellValue(result.getTestEndTime());
                row.createCell(7).setCellValue(result.getTestExecutionTime());
                row.createCell(8).setCellValue(result.getTestExecutionType());
                row.createCell(9).setCellValue(result.getThreadCount());
            }

            for(int i=0; i<headers.length; i++){
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(PropertyReader.readProperty("testResultExcelPath"))) {
                workbook.write(fileOut);
            }

            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CellStyle getHeaderCellStyle(Workbook workBook){
        CellStyle style = workBook.createCellStyle();
        Font headerFont = workBook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

}


