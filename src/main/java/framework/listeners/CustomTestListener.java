package framework.listeners;

import com.aventstack.extentreports.Status;
import framework.utils.ExtentReport;
import framework.utils.ExceptionMessageLoader;
import framework.base.DriverFactory;
import org.testng.*;

import static framework.utils.ScreenShotUtil.captureScreenshot;
import framework.base.TestBase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public class CustomTestListener implements ITestListener, ISuiteListener {

    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private static final Logger logger = LogManager.getLogger(CustomTestListener.class);

    @Override
    public void onStart(ISuite suite) {
        logger.info("Suite started: {}", suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        Collection<ISuiteResult> results = suite.getResults().values();
        if (!results.isEmpty()) {
            ITestContext context = results.iterator().next().getTestContext();
            String environment = getParameterOrSystemProperty(context, "env");
            String browser = getParameterOrSystemProperty(context, "browser");
            boolean parallel = isParallelExecution(context);
            int threadCount = getThreadCount(context);

            ExtentReport.getDetailedExtent().setSystemInfo("Environment", environment);
            ExtentReport.getDetailedExtent().setSystemInfo("Browser", browser);
            ExtentReport.getDetailedExtent().setSystemInfo("Execution Mode", parallel ? "Parallel" : "Sequential");
            ExtentReport.getDetailedExtent().setSystemInfo("Thread Count", String.valueOf(threadCount));
            ExtentReport.flushReports();
        } else {
            System.out.println("No suite results found.");
        }

    }

    @Override
    public void onTestStart(ITestResult result) {
        startTime.set(System.currentTimeMillis());
        ExtentReport.setDetailedTest(
                ExtentReport.getDetailedExtent().createTest(result.getMethod().getMethodName())
        );
        ExtentReport.setEmailableTest(
                ExtentReport.getEmailableExtent().createTest(result.getMethod().getMethodName())
        );
        logger.info("Test started: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReport.getDetailedTest().pass("Test Passed");
        ExtentReport.getEmailableTest().pass("Test Passed");
        logger.info("Test passed: {}", result.getMethod().getMethodName());
        logExecutionTime(result);

        String environment = getParameterOrSystemProperty(result.getTestContext(), "env");
        String browser = getParameterOrSystemProperty(result.getTestContext(), "browser");

        ExtentReport.addSummaryData(
                result,
                environment,
                browser,
                isParallelExecution(result.getTestContext()) ? "Parallel" : "Sequential",
                getThreadCount(result.getTestContext())
        );
        TestBase.clearTestData();
        removeAllTests();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable throwable = result.getThrowable();
        ExtentReport.getDetailedTest().fail("Test Failed: " + throwable);
        ExtentReport.getEmailableTest().fail("Test Failed: " + throwable);

        String friendlyMessage = ExceptionMessageLoader.getMessageForException(throwable);
        ExtentReport.getDetailedTest().log(Status.INFO, "Reason: " + friendlyMessage);
        ExtentReport.getEmailableTest().log(Status.INFO, "Reason: " + friendlyMessage);

        logger.error("Test failed: {}", result.getMethod().getMethodName(), throwable);
        logger.error("Reason: {}", friendlyMessage);

        if (DriverFactory.getDriver() != null) {
            captureScreenshot(Status.FAIL, throwable.getMessage());
        }

        logExecutionTime(result);

        String environment = getParameterOrSystemProperty(result.getTestContext(), "env");
        String browser = getParameterOrSystemProperty(result.getTestContext(), "browser");

        ExtentReport.addSummaryData(
                result,
                environment,
                browser,
                isParallelExecution(result.getTestContext()) ? "Parallel" : "Sequential",
                getThreadCount(result.getTestContext())
        );
        TestBase.clearTestData();
        removeAllTests();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Throwable skipReason = result.getThrowable();
        ExtentReport.getDetailedTest().skip("Test Skipped" + (skipReason != null ? ": " + skipReason : ""));
        ExtentReport.getEmailableTest().skip("Test Skipped" + (skipReason != null ? ": " + skipReason : ""));
        logger.warn("Test skipped: {}", result.getMethod().getMethodName());
        logExecutionTime(result);

        String environment = getParameterOrSystemProperty(result.getTestContext(), "env");
        String browser = getParameterOrSystemProperty(result.getTestContext(), "browser");

        ExtentReport.addSummaryData(
                result,
                environment,
                browser,
                isParallelExecution(result.getTestContext()) ? "Parallel" : "Sequential",
                getThreadCount(result.getTestContext())
        );
        TestBase.clearTestData();
        removeAllTests();
    }

    private void logExecutionTime(ITestResult result) {
        if (startTime.get() != null) {
            long duration = System.currentTimeMillis() - startTime.get();
            long seconds = (duration / 1000) % 60;
            long minutes = (duration / (1000 * 60)) % 60;
            long hours = (duration / (1000 * 60 * 60));

            String time = String.format("%02dh:%02dm:%02ds", hours, minutes, seconds);
            ExtentReport.getDetailedTest().log(Status.INFO, "Execution Time: " + time);
            ExtentReport.getEmailableTest().log(Status.INFO, "Execution Time: " + time);
            logger.info("Execution time for {}: {}", result.getMethod().getMethodName(), time);

            startTime.remove();
        }
    }

    public static void removeAllTests() {
        framework.utils.ExtentReport.removeDetailedTest();
        framework.utils.ExtentReport.removeEmailableTest();
    }

    public static boolean isParallelExecution(ITestContext context) {
        String parallelMode = context.getCurrentXmlTest().getSuite().getParallel().toString();
        return !"NONE".equalsIgnoreCase(parallelMode) && !"false".equalsIgnoreCase(parallelMode);
    }

    public static int getThreadCount(ITestContext context) {
        return context.getCurrentXmlTest().getSuite().getThreadCount();
    }

    private String getParameterOrSystemProperty(ITestContext context, String paramName) {
        String value = context.getCurrentXmlTest().getParameter(paramName);
        if (value == null || value.trim().isEmpty()) {
            value = System.getProperty(paramName, "N/A");
        }
        return value;
    }
}
