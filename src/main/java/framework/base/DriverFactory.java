package framework.base;

import framework.config.PropertyReader;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import java.time.Duration;


public class DriverFactory {

    private static ThreadLocal<WebDriverWait> wait = new ThreadLocal<>();
    public static ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static void setDriver(String strBrowserType, String osName) {
        try {
            switch (strBrowserType) {
                case "Chrome":
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--window-size=1920,1080");
                    options.addArguments("--remote-allow-origins=*");
                    options.addArguments("--ignore-certificate-errors");
                    options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                    if (PropertyReader.readProperty("useSeleniumManager").equalsIgnoreCase("true")) {
                        System.out.println("########## Using Selenium Manager Driver ###########");
                        WebDriver driver = new ChromeDriver(options);
                        driverThreadLocal.set(driver);
                        wait.set(new WebDriverWait(driver, Duration.ofSeconds(25)));
                    } else {
                        System.out.println("########## Using Manually Downloaded Driver ###########");
                        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/driver/chromedriver.exe");
                        WebDriver driver = new ChromeDriver(options);
                        driverThreadLocal.set(driver);
                        wait.set(new WebDriverWait(driver, Duration.ofSeconds(25)));
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not open the browser");
        }
    }

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void quitDriver() {
        if (driverThreadLocal.get() != null) {
            driverThreadLocal.get().quit();
            driverThreadLocal.remove();
        }
    }

    public static WebDriverWait getWait() {
        return wait.get();
    }

}
