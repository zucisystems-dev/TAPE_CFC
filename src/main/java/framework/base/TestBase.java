package framework.base;

import framework.listeners.CustomTestListener;
import framework.utils.ExtentReport;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;


import java.lang.reflect.Method;
import java.util.Map;

import static framework.base.PageObjectInitiator.apiObjectInitiator;
import static framework.base.PageObjectInitiator.objectInitiator;
import static framework.config.PropertyReader.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TestBase {

    private static String url;
    private static final ThreadLocal<String> browserName = new ThreadLocal<>();
    private static final Logger logger = LogManager.getLogger(TestBase.class);

    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

    public static void setStartTime(Long time) {
        startTime.set(time);
    }

    public static Long getStartTime() {
        return startTime.get();
    }

    public static void removeStartTime() {
        startTime.remove();
    }

    public static void setBrowserName(String b) {
        browserName.set(b);
    }

    public static String getBrowserName() {
        return browserName.get();
    }

    public static void removeBrowserName() {
        browserName.remove();
    }

    public static String getUrl() {
        return url;
    }

    @BeforeSuite(alwaysRun = true)
    @Parameters("env")
    public void setupReport(String env) {
        String[] environment = env.split("_");
        url = getPropertyFileURL(env);
        ExtentReport.createInstances(environment[0]);
        logger.info("Before Sui;te: Opening browser...");
    }

    @BeforeMethod
    public void beforeMethod(Method method, ITestContext context) {
        setBrowserName(context.getCurrentXmlTest().getParameter("browser"));
        if (getBrowserName().equalsIgnoreCase("chrome")) {
            setStartTime(System.currentTimeMillis());
            DriverFactory.setDriver(getBrowserName(), System.getProperty("os.name"));
            WebDriver driver = DriverFactory.getDriver();
            objectInitiator(driver);
            driver.manage().window().maximize();
            driver.get(getUrl());
        } else if (getBrowserName().equalsIgnoreCase("api")) {
            logger.info("Before Method: API Testing Starts");
            setStartTime(System.currentTimeMillis());
            apiObjectInitiator();
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result, Method method, ITestContext context) {
        if (getBrowserName().equalsIgnoreCase("chrome")) {
            logger.info("After Method: Quitting browser...");
            try {
                DriverFactory.quitDriver();
            } catch (Exception e) {
                logger.error("Error during driver quit", e);
            }
            try {
                calculateExecutionTime(method);
            } catch (Exception e) {
                logger.error("Error in calculateExecutionTime", e);
            }
            CustomTestListener.removeAllTests();
        } else if (getBrowserName().equalsIgnoreCase("api")) {
            logger.info("After Method: API Testing Ends");
            try {
                calculateExecutionTime(method);
            } catch (Exception e) {
                logger.error("Error in calculateExecutionTime", e);
            }
            CustomTestListener.removeAllTests();
        }
        removeBrowserName();
        clearTestData();
    }


    private static final ThreadLocal<Map<String, String>> testDataThreadLocal = new ThreadLocal<>();

    public static void setTestData(Map<String, String> data) {
        testDataThreadLocal.set(data);
    }

    public static Map<String, String> getTestData() {
        return testDataThreadLocal.get();
    }

    public static void clearTestData() {
        testDataThreadLocal.remove();
    }

    public static void calculateExecutionTime(Method method) {
        if (getStartTime() == null) {
            logger.warn("Start time was null for test [{}]", method.getName());
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - getStartTime();
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;

        logger.info("{} - Test Execution Time : {}h:{}m:{}s",
                method.getName(), hours, minutes, seconds);

        removeStartTime();
    }

}
