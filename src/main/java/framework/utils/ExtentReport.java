package framework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestResult;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ExtentReport {

    private static final ThreadLocal<ExtentTest> detailedTestThread = new ThreadLocal<>();
    private static final ThreadLocal<ExtentTest> emailableTestThread = new ThreadLocal<>();

    private static ExtentReports detailedExtent;
    private static ExtentReports emailableExtent;

    private static final ConcurrentLinkedQueue<String[]> suiteSummaryData = new ConcurrentLinkedQueue<>();

    public static final String TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

    private ExtentReport() {
        throw new UnsupportedOperationException("Extent Report class — do not instantiate.");
    }

    public static void setDetailedTest(ExtentTest test) {
        detailedTestThread.set(test);
    }
    public static ExtentTest getDetailedTest() {
        return detailedTestThread.get();
    }
    public static void removeDetailedTest() {
        detailedTestThread.remove();
    }

    public static void setEmailableTest(ExtentTest test) {
        emailableTestThread.set(test);
    }
    public static ExtentTest getEmailableTest() {
        return emailableTestThread.get();
    }
    public static void removeEmailableTest() {
        emailableTestThread.remove();
    }

    public static void createInstances(String app) {
        String reportFolderPath = "reports" + File.separator + TIMESTAMP + File.separator;
        String screenshotFolderPath = reportFolderPath + "Screenshots" + File.separator;

        new File(reportFolderPath).mkdirs();
        new File(screenshotFolderPath).mkdirs();

        // Detailed report path
        String detailedReportPath = reportFolderPath + app + "_Main_Report_" + TIMESTAMP + ".html";
        ExtentSparkReporter detailedReporter = new ExtentSparkReporter(detailedReportPath);
        detailedReporter.config().setReportName("Automation Test Report");
        detailedReporter.config().setDocumentTitle("Extent Report - Dark Theme");
        detailedReporter.config().setTheme(Theme.DARK);

        // Emailable report path
        String emailableReportPath = reportFolderPath + app + "_Emailable_Report.html";
        ExtentSparkReporter emailableReporter = new ExtentSparkReporter(emailableReportPath);
        emailableReporter.config().setReportName("Emailable Test Summary");
        emailableReporter.config().setDocumentTitle("Emailable Automation Report");
        emailableReporter.config().setTheme(Theme.DARK);

        detailedExtent = new ExtentReports();
        detailedExtent.attachReporter(detailedReporter);

        emailableExtent = new ExtentReports();
        emailableExtent.attachReporter(emailableReporter);
    }

    public static ExtentReports getDetailedExtent() {
        return detailedExtent;
    }

    public static ExtentReports getEmailableExtent() {
        return emailableExtent;
    }

    public static void flushReports() {
        if (detailedExtent != null) detailedExtent.flush();
        if (emailableExtent != null) emailableExtent.flush();
    }

    public static void addSummaryData(ITestResult result, String environment, String browser,
                                      String executionMode, int threadCount) {
        String status = result.getStatus() == ITestResult.SUCCESS ? "PASS"
                : result.getStatus() == ITestResult.FAILURE ? "FAIL"
                : "SKIP";

        String failureReason = "";
        if (result.getThrowable() != null) {
            failureReason = result.getThrowable().getMessage();
        }

        long executionTime = (result.getEndMillis() - result.getStartMillis()) / 1000;

        suiteSummaryData.add(new String[]{
                result.getMethod().getMethodName(),
                environment,
                browser,
                status,
                failureReason,
                String.valueOf(executionTime),
                executionMode,
                String.valueOf(threadCount)
        });
    }

    public static void writeSuiteSummary(String environment, String browser,
                                         String executionMode, int threadCount) {
        if (suiteSummaryData.isEmpty()) return;

        int totalPassed = 0, totalFailed = 0, totalSkipped = 0;
        StringBuilder summaryTableHtml = new StringBuilder();
        summaryTableHtml.append("<table border='1' style='border-collapse:collapse; width:100%'>")
                .append("<tr style='background-color:#f2f2f2'><th>Test Method</th><th>Status</th><th>Failure Reason</th><th>Execution Time (s)</th></tr>");

        for (String[] row : suiteSummaryData) {
            String status = row[3];
            if ("PASS".equalsIgnoreCase(status)) totalPassed++;
            else if ("FAIL".equalsIgnoreCase(status)) totalFailed++;
            else if ("SKIP".equalsIgnoreCase(status)) totalSkipped++;

            summaryTableHtml.append("<tr>")
                    .append("<td>").append(row[0]).append("</td>")
                    .append("<td>").append(status).append("</td>")
                    .append("<td>").append(row[4] == null ? "" : row[4]).append("</td>")
                    .append("<td>").append(row[5]).append("</td>")
                    .append("</tr>");
        }
        summaryTableHtml.append("</table>");

        int totalTests = totalPassed + totalFailed + totalSkipped;
        double passPercent = totalTests > 0 ? totalPassed * 100.0 / totalTests : 0;
        double failPercent = totalTests > 0 ? totalFailed * 100.0 / totalTests : 0;

        // Detailed report system info
        detailedExtent.setSystemInfo("Environment", environment);
        detailedExtent.setSystemInfo("Browser", browser);
        detailedExtent.setSystemInfo("Execution Mode", executionMode);
        detailedExtent.setSystemInfo("Thread Count", String.valueOf(threadCount));
        detailedExtent.setSystemInfo("Total Passed", String.valueOf(totalPassed));
        detailedExtent.setSystemInfo("Total Failed", String.valueOf(totalFailed));
        detailedExtent.setSystemInfo("Total Skipped", String.valueOf(totalSkipped));
        detailedExtent.setSystemInfo("Pass %", String.format("%.2f", passPercent));
        detailedExtent.setSystemInfo("Fail %", String.format("%.2f", failPercent));
        detailedExtent.setSystemInfo("OS", System.getProperty("os.name"));
        detailedExtent.setSystemInfo("Java Version", System.getProperty("java.version"));
        detailedExtent.setSystemInfo("Executed By", System.getProperty("user.name"));

    }
}
