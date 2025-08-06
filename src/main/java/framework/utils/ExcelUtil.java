package framework.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
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
    }


