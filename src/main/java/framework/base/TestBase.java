package framework.base;

import framework.utils.ExceptionMessageLoader;
import framework.utils.ExtentReport;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.Map;

import static framework.base.PageObjectInitiator.apiObjectInitiator;
import static framework.base.PageObjectInitiator.objectInitiator;
import static framework.config.PropertyReader.*;
import static framework.utils.ExtentReport.removeTest;
import static framework.utils.ScreenShotUtil.captureScreenshot;

public class TestBase {

    public static String url = "";
    public static ThreadLocal<Long> startTime = new ThreadLocal<>();
    public static long endTime;
    public static String browserName;

    @BeforeSuite(alwaysRun = true)
    @Parameters("env")
    public void setupReport(String env) {
        String[] environment = env.split("_");
        url = getPropertyFileURL(env);
        ExtentReport.getInstance(environment[0]);
        System.out.println("Before Suite: Opening browser...");
    }

    @BeforeMethod
    public void beforeMethod(Method method, ITestContext context) {
        browserName = context.getCurrentXmlTest().getParameter("browser");
        if(browserName.equalsIgnoreCase("chrome")) {
            startTime.set(System.currentTimeMillis());
            ExtentTest test = ExtentReport.getExtent().createTest(method.getName());
            ExtentReport.setTest(test);
            DriverFactory.setDriver(browserName, System.getProperty("os.name"));
            WebDriver driver;
            driver = DriverFactory.getDriver();
            objectInitiator(driver);
            driver.manage().window().maximize();
            driver.get(url);
        }
        else if(browserName.equalsIgnoreCase("api")){
            System.out.println("Before Method: API Testing Starts");
            startTime.set(System.currentTimeMillis());
            ExtentTest test = ExtentReport.getExtent().createTest(method.getName());
            ExtentReport.setTest(test);
            apiObjectInitiator();
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result, Method method, ITestContext context) {
        browserName = context.getCurrentXmlTest().getParameter("browser");
        if(browserName.equalsIgnoreCase("chrome")) {
            System.out.println("After Method: Quitting browser...");
            WebDriver driver = DriverFactory.getDriver();
            try {
                if (result.getStatus() == ITestResult.FAILURE) {
                    try {
                        Throwable throwable = result.getThrowable();
                        ExtentReport.getTest().fail("Test Failed: " + throwable);

                        String friendlyMessage = ExceptionMessageLoader.getMessageForException(throwable);
                        System.out.println("Test Case Failed: " + method.getName());
                        System.out.println("Exception: " + throwable.getClass().getSimpleName());
                        System.out.println("Reason: " + friendlyMessage);

                        if (driver != null) {
                            captureScreenshot(Status.FAIL, result.getThrowable().getMessage());
                        }
                    } catch (Exception e) {
                        System.err.println("Error capturing screenshot or logging failure: " + e.getMessage());
                        e.printStackTrace();
                    }

                } else if (result.getStatus() == ITestResult.SUCCESS) {
                    try {
                        ExtentReport.getTest().pass("Test Passed");
                    } catch (Exception e) {
                        System.err.println("Error logging test pass: " + e.getMessage());
                    }

                } else if (result.getStatus() == ITestResult.SKIP) {
                    try {
                        ExtentReport.getTest().skip("Test Skipped: " + result.getThrowable());
                    } catch (Exception e) {
                        System.err.println("Error logging test skip: " + e.getMessage());
                    }
                }
            } finally {
                try {
                    DriverFactory.quitDriver();
                } catch (Exception e) {
                    System.err.println("Error during driver quit: " + e.getMessage());
                    e.printStackTrace();
                }
                try {
                    calculateExecutionTime(method);
                } catch (Exception e) {
                    System.err.println("Error in calculateExecutionTime: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            removeTest();
        }
        else if(browserName.equalsIgnoreCase("api")){
            System.out.println("After Method: API Testing Ends");
            try {
                if (result.getStatus() == ITestResult.FAILURE) {
                    try {
                        Throwable throwable = result.getThrowable();
                        ExtentReport.getTest().fail("Test Failed: " + throwable);

                        String friendlyMessage = ExceptionMessageLoader.getMessageForException(throwable);
                        System.out.println("Test Case Failed: " + method.getName());
                        System.out.println("Exception: " + throwable.getClass().getSimpleName());
                        System.out.println("Reason: " + friendlyMessage);

                    } catch (Exception e) {
                        System.err.println("Error logging failure: " + e.getMessage());
                        e.printStackTrace();
                    }

                } else if (result.getStatus() == ITestResult.SUCCESS) {
                    try {
                        ExtentReport.getTest().pass("Test Passed");
                    } catch (Exception e) {
                        System.err.println("Error logging test pass: " + e.getMessage());
                    }

                } else if (result.getStatus() == ITestResult.SKIP) {
                    try {
                        ExtentReport.getTest().skip("Test Skipped: " + result.getThrowable());
                    } catch (Exception e) {
                        System.err.println("Error logging test skip: " + e.getMessage());
                    }
                }
            } finally {
                try {
                    calculateExecutionTime(method);
                } catch (Exception e) {
                    System.err.println("Error in calculateExecutionTime: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            removeTest();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownReport() {
        ExtentReport.flushReports();
    }

    private static final ThreadLocal<Map<String, String>> testDataThreadLocal = new ThreadLocal<>();

    public static void setTestData(Map<String, String> data) {
        testDataThreadLocal.set(data);
    }

    public static Map<String, String> getTestData() {
        return testDataThreadLocal.get();
    }

    public static void clear() {
        testDataThreadLocal.remove();
    }

    public static void calculateExecutionTime(Method method){
        if (startTime.get() == null) {
            System.out.printf("Start time was null for test [%s]%n", method.getName());
        }
        endTime = System.currentTimeMillis();
        long duration = endTime - startTime.get();
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;

        System.out.printf(method.getName() + " - Test Execution Time : %02dh:%02dm:%02ds%n", hours, minutes, seconds);

        startTime.remove();
    }

}
