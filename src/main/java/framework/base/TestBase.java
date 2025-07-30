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

import static framework.base.PageObjectInitiator.objectInitiator;
import static framework.config.PropertyReader.*;
import static framework.utils.ScreenShotUtil.captureScreenshot;

public class TestBase {

    public static String url = "";

    @BeforeSuite(alwaysRun = true)
    @Parameters("env")
    public void setupReport(String env) {
        String[] environment = env.split("_");
        url = getPropertyFileURL(env);
        ExtentReport.getInstance(environment[0]);
    }

    @BeforeMethod
    public void beforeMethod(Method method, ITestContext context) {
        String browserName = context.getCurrentXmlTest().getParameter("browser");
        System.out.println("Before Method: Opening browser...");
        ExtentTest test = ExtentReport.getExtent().createTest(method.getName());
        ExtentReport.setTest(test);
        DriverFactory.setDriver(browserName, System.getProperty("os.name"));
        WebDriver driver;
        driver = DriverFactory.getDriver();
        objectInitiator(driver);
        driver.manage().window().maximize();
        driver.get(url);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result, Method method) {
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

}
