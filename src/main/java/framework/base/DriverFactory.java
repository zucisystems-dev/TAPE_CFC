package framework.base;

import framework.config.PropertyReader;
import org.openqa.selenium.Dimension;
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

    private DriverFactory(){
        throw new UnsupportedOperationException("Driver class — do not instantiate.");
    }

    public static void setDriver(String strBrowserType) {
        try {
            switch (strBrowserType) {
                case "Chrome":
                    ChromeOptions options = new ChromeOptions();
                    if(PropertyReader.readProperty("headless").equalsIgnoreCase("true")){
                        options.addArguments("--headless=new");
                    }
                    options.addArguments("--window-size=1920,1080");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--remote-allow-origins=*");
                    options.addArguments("--ignore-certificate-errors");
                    options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                    if (PropertyReader.readProperty("useSeleniumManager").equalsIgnoreCase("true")) {
                        System.out.println("########## Using Selenium Manager Driver ###########");
                            WebDriver driver = new ChromeDriver(options);
                            driver.manage().window().setSize(new Dimension(1920, 1080));
                            driverThreadLocal.set(driver);
                            wait.set(new WebDriverWait(driver, Duration.ofSeconds(30)));
                    } else {
                        System.out.println("########## Using Manually downloaded Driver ###########");
                        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/driver/chromedriver.exe");
                        WebDriver driver = new ChromeDriver(options);
                        driver.manage().window().setSize(new Dimension(1920, 1080));
                        driverThreadLocal.set(driver);
                        wait.set(new WebDriverWait(driver, Duration.ofSeconds(30)));
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
            wait.remove();
        }
    }

    public static WebDriverWait getWait() {
        return wait.get();
    }

}
