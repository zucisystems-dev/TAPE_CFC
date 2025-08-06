package framework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReport {
    private static final ThreadLocal<ExtentTest> extentTestThread = new ThreadLocal<>();
    private static ExtentReports extent;
    public static String today = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

    private ExtentReport(){
        throw new UnsupportedOperationException("Extent Report class — do not instantiate.");
    }

    public static void setTest(ExtentTest test) {
        extentTestThread.set(test);
    }

    public static ExtentTest getTest() {
        return extentTestThread.get();
    }

    public static void removeTest(){ extentTestThread.remove();}

    public static ExtentReports getInstance(String app) {
        if (extent == null) {
            createInstance(app);
        }
        return extent;
    }

    public static ExtentReports getExtent() {
        return extent;
    }

    private static void createInstance(String app) {

        String reportFolderPath = "reports\\" + today + "\\";
        String screenshotFolderPath = reportFolderPath + "Screenshots\\";

        new File(reportFolderPath).mkdirs();
        new File(screenshotFolderPath).mkdirs();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String reportPath = reportFolderPath + app +"_Report_" + timestamp + ".html";
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

        sparkReporter.config().setReportName("Automation Test Report");
        sparkReporter.config().setDocumentTitle("Extent Report - Dark Theme");
        sparkReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Environment", "QA");
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }
}