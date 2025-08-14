package framework.base;

import framework.config.PropertyReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;


public class DriverFactory {

    private static ThreadLocal<WebDriverWait> wait = new ThreadLocal<>();
    public static ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverFactory(){
        throw new UnsupportedOperationException("Driver class — do not instantiate.");
    }

    public static void setDriver(String strBrowserType) {
        try {
            DesiredCapabilities caps = new DesiredCapabilities();
            String user = PropertyReader.getProperty("config","bs_user");
            String key  = PropertyReader.getProperty("config","bs_key");
            MutableCapabilities capabilities = new MutableCapabilities();
            HashMap<String, Object> stackOptions = new HashMap<>();
            String url = "https://" + user + ":" + key + "@hub-cloud.browserstack.com/wd/hub";

            switch (strBrowserType) {
                case "chrome":
                    ChromeOptions chromeOptions = new ChromeOptions();
                    if(PropertyReader.readProperty("headless").equalsIgnoreCase("true")){
                        chromeOptions.addArguments("--headless=new");
                    }
                    chromeOptions.addArguments("--window-size=1920,1080");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--remote-allow-origins=*");
                    chromeOptions.addArguments("--ignore-certificate-errors");
                    chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                    if (PropertyReader.readProperty("useSeleniumManager").equalsIgnoreCase("true")) {
                        System.out.println("########## Using Selenium Manager Chrome Driver ###########");
                        WebDriver driver = new ChromeDriver(chromeOptions);
                        driver.manage().window().setSize(new Dimension(1920, 1080));
                        driverThreadLocal.set(driver);
                        wait.set(new WebDriverWait(driver, Duration.ofSeconds(30)));
                    } else {
                        System.out.println("########## Using Manually downloaded Chrome Driver ###########");
                        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/driver/chromedriver.exe");
                        WebDriver driver = new ChromeDriver(chromeOptions);
                        driver.manage().window().setSize(new Dimension(1920, 1080));
                        driverThreadLocal.set(driver);
                        wait.set(new WebDriverWait(driver, Duration.ofSeconds(30)));
                    }
                    break;
                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();
                    if(PropertyReader.readProperty("headless").equalsIgnoreCase("true")){
                        edgeOptions.addArguments("--headless=new");
                    }
                    edgeOptions.addArguments("--window-size=1920,1080");
                    edgeOptions.addArguments("--no-sandbox");
                    edgeOptions.addArguments("--disable-gpu");
                    edgeOptions.addArguments("--disable-dev-shm-usage");
                    edgeOptions.addArguments("--remote-allow-origins=*");
                    edgeOptions.addArguments("--ignore-certificate-errors");
                    edgeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                    /*if (PropertyReader.readProperty("useSeleniumManager").equalsIgnoreCase("true")) {
                        System.out.println("########## Using Selenium Manager Edge Driver ###########");
                        WebDriver driver = new EdgeDriver(edgeOptions);
                        driver.manage().window().setSize(new Dimension(1920, 1080));
                        driverThreadLocal.set(driver);
                        wait.set(new WebDriverWait(driver, Duration.ofSeconds(30)));
                    } else {*/
                    System.out.println("########## Using Manually downloaded Edge Driver ###########");
                    System.setProperty("webdriver.edge.driver", System.getProperty("user.dir") + "/driver/msedgedriver.exe");
                    WebDriver driver = new EdgeDriver(edgeOptions);
                    driver.manage().window().setSize(new Dimension(1920, 1080));
                    driverThreadLocal.set(driver);
                    wait.set(new WebDriverWait(driver, Duration.ofSeconds(30)));
                    break;
                case "android_app":
                    try {
                        caps.setCapability("browserstack.user", user);
                        caps.setCapability("browserstack.key", key);

                        caps.setCapability("platformName", "Android");
                        caps.setCapability("deviceName", PropertyReader.getProperty("config","android_device"));
                        caps.setCapability("os_version", PropertyReader.getProperty("config","android_os_version"));
                        caps.setCapability("unicodeKeyboard", true);
                        caps.setCapability("resetKeyboard", true);
                        caps.setCapability("interactiveDebugging", true);
                        caps.setCapability("app", PropertyReader.getProperty("ECS","android_app_url").trim());

                        caps.setCapability("project", "ECS Regression");
                        caps.setCapability("build", "Demo Build");
                        caps.setCapability("name", "Android Demo");

                        driverThreadLocal.set(new AndroidDriver(new URL(url), caps));
                        wait.set(new WebDriverWait(driverThreadLocal.get(), Duration.ofSeconds(30)));

                    } catch (Exception e) {
                        throw new RuntimeException("Failed to initialize BrowserStack mobile driver: " + e.getMessage(), e);
                    }
                    break;
                case "ios_app":
                    try {
                        caps.setCapability("browserstack.user", user);
                        caps.setCapability("browserstack.key", key);

                        caps.setCapability("platformName", "iOS");
                        caps.setCapability("deviceName", PropertyReader.getProperty("config","ios_device"));
                        caps.setCapability("os_version", PropertyReader.getProperty("config","ios_os_version"));
                        caps.setCapability("app", PropertyReader.getProperty("ECS","ios_app_url"));
                        caps.setCapability("disableKeyboard", true);
                        caps.setCapability("connectHardwareKeyboard", true);
                        caps.setCapability("project", "ECS Regression");
                        caps.setCapability("build", "Build 001");
                        caps.setCapability("name", "iOS Test");

                        driverThreadLocal.set(new IOSDriver(new URL(url), caps));
                        wait.set(new WebDriverWait(driverThreadLocal.get(), Duration.ofSeconds(30)));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to initialize BrowserStack iOS driver: " + e.getMessage(), e);
                    }
                    break;
                case "android_chrome":
                    capabilities.setCapability("browserName", "chrome");
                    stackOptions.put("osVersion", PropertyReader.getProperty("config","android_os_version"));
                    stackOptions.put("deviceName", PropertyReader.getProperty("config","android_device"));
                    stackOptions.put("userName", user);
                    stackOptions.put("accessKey", key);
                    stackOptions.put("consoleLogs", "info");
                    stackOptions.put("projectName", "ECS Android - Web Automation");
                    stackOptions.put("buildName", "Mobile Web Regression");
                    stackOptions.put("sessionName", "ECS Scripts");
                    capabilities.setCapability("bstack:options", stackOptions);
                    driverThreadLocal.set(new RemoteWebDriver(new URL(url), capabilities));
                    wait.set(new WebDriverWait(driverThreadLocal.get(), Duration.ofSeconds(30)));
                    break;
                case "ios_safari":
                    capabilities = new MutableCapabilities();
                    capabilities.setCapability("browserName", "safari");
                    stackOptions.put("osVersion", PropertyReader.getProperty("config","ios_os_version"));
                    stackOptions.put("deviceName", PropertyReader.getProperty("config","ios_device"));
                    stackOptions.put("userName", user);
                    stackOptions.put("accessKey", key);
                    stackOptions.put("projectName", "ECS IOS - Web Testing");
                    stackOptions.put("buildName", "Mobile Web Regression");
                    stackOptions.put("sessionName", "ECS Scripts");
                    capabilities.setCapability("bstack:options", stackOptions);
                    driverThreadLocal.set(new RemoteWebDriver(new URL(url), capabilities));
                    wait.set(new WebDriverWait(driverThreadLocal.get(), Duration.ofSeconds(30)));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
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
