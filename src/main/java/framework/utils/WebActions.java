package framework.utils;

import framework.base.DriverFactory;
import framework.base.TestBase;
import com.aventstack.extentreports.Status;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class WebActions extends TestBase {

    private static WebDriver driver = DriverFactory.getDriver();
    private static WebDriverWait wait = DriverFactory.getWait();
    private static Actions action;

    public static boolean scrolltoElement(WebElement object) {
        try {
            JavascriptExecutor js;
            js = (JavascriptExecutor) DriverFactory.getDriver();
            js.executeScript("arguments[0].scrollIntoView(true);", object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void waitTillElementVisible(WebElement ele) {
        DriverFactory.getWait().until(ExpectedConditions.visibilityOf(ele));

    }

    public static void waitTillElementClickable(WebElement ele) {
        DriverFactory.getWait().until(ExpectedConditions.elementToBeClickable(ele));

    }

    public static void logMessageInReport(String message){
        ExtentReport.getTest().log(Status.PASS, message);
    }

    private static void setLocalDriver() {
        driver = DriverFactory.getDriver();
        wait = DriverFactory.getWait();
    }

    private static void setAction() {
        if (action != null)
            action = null;

        action = new Actions(DriverFactory.getDriver());
    }

    /**
     * Got to the given URl
     *
     * @param url
     */
    public static void goToUrl(String url) throws IOException {
        //setLocalDriver();
        if (System.getProperty("browser").equalsIgnoreCase("CHROME")) {
            DriverFactory.getDriver().get(url);
        }
    }

    public static String getUrl() {
        return DriverFactory.getDriver().getCurrentUrl();
    }

    public static String getNetworkEntries() {
        String scriptToExecute = "var performance = window.performance || window.mozPerformance || window.msPerformance || window.webkitPerformance || {}; var network = performance.getEntries() || {}; return network;";
        return ((JavascriptExecutor) DriverFactory.getDriver()).executeScript(scriptToExecute).toString();
    }

    public static void closeWindow() {
        driver.close();
    }

    public static void invisibilityOfElement(String locator) {
        WebDriverWait w = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(30));
        w.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(locator)));
    }

    public static void visibilityOfElement(By locator) {
        WebDriverWait w = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(30));
        w.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Switches between browser windows or tabs.
     *
     * @throws NoSuchWindowException If the specified window does not exist.
     */
    public static void windowHandleSwitch() throws NoSuchWindowException {
        Set<String> windowHandles = DriverFactory.getDriver().getWindowHandles();
        for (String handle : windowHandles) {
            if (!handle.equals(DriverFactory.getDriver().getWindowHandle())) {
                DriverFactory.getDriver().switchTo().window(handle);
                break;
            }
        }
    }

    public static String getWindowHandle() {
        return DriverFactory.getDriver().getWindowHandle();
    }

    public static void windowHandleSwitchParent(String handle) {
        DriverFactory.getDriver().switchTo().window(handle);
    }

    public static void switchToIframe(WebElement element) {
        DriverFactory.getDriver().switchTo().frame(element);
    }

    public static void forceClick(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
        js.executeScript("arguments[0].click();", element);
    }


    public static boolean elementIsDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Perform mouseOver an click on the given element
     *
     * @param elem: Elemnt to be clicked
     */
    public static void clickOnElement(WebElement elem) {
        setAction();
        JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
        js.executeScript("arguments[0].scrollIntoView();", elem);
        action.moveToElement(elem).build().perform();
        elem.click();
    }

    public static void clickElementJS(WebElement element, String message) {
        waitTillElementClickable(element);
        setAction();
        JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
        try {
            js.executeScript("arguments[0].scrollIntoView();", element);
            action.moveToElement(element).build().perform();
            WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(5));
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
            logMessageInReport(message);
        } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
            try {
                js.executeScript("arguments[0].click();", element);
                WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(5));
                wait.until(ExpectedConditions.elementToBeClickable(element)).click();
                logMessageInReport(message);
            } catch (Exception jsClickException) {
                System.out.println("Failed to click element using all strategies.");
            }
        }
    }

    public static void clickElement(WebElement element, String message){
        waitTillElementClickable(element);
        element.click();
    }

    public static void doubleClickElement(WebElement element) {
        setAction();
        action.doubleClick(element).perform();
    }

    /**
     * Perform mouseOver on the given element
     *
     * @param elem: Elemnt to be focus
     */
    public static void hoverOnElement(WebElement elem) {
        setAction();
        wait.until(ExpectedConditions.elementToBeClickable(elem));
        action.moveToElement(elem).build().perform();
    }

    /**
     * Set text via SendKeys
     *
     * @param text:           Text to be setted
     * @param element:Element to set
     */
    public static void setText(WebElement element, String text, String message) {
        element.clear();
        element.sendKeys(text);
        logMessageInReport(message);
    }

    /**
     * Select an item from select element
     *
     * @param type:   kind of attribute to select
     * @param select: Element to select(list of values)
     * @param text:   Value to select on the list
     */
    public static void selectBy(Select select, String type, String text) {
        switch (type.toUpperCase()) {
            case "VALUE":
                select.selectByValue(text);
                break;
            case "VISIBLE_TEXT":
                select.selectByVisibleText(text);
                break;
            case "INDEX":
                select.selectByIndex(Integer.parseInt(text));
                break;
        }
    }

    /**
     * GET TEXT FROM A ELEMENT
     *
     * @param elem: Element to obtain the text attribute
     *              return: A string
     */
    public static String getElementText(WebElement elem) {
        String text;
        do {
            text = elem.getText();
        } while (text.equals(""));

        return elem.getText();
    }

    /**
     * GET ATRIBUTE FROM A ELEMENT
     *
     * @param elem: Element to obtain the attribute
     *              return: Attribute requested as string
     */
    public static String getElementAttribute(WebElement elem, String attribute) {
        return elem.getAttribute(attribute);
    }

    public static void waitForElementBeDisplayed(WebElement element) throws InterruptedException {
        //setLocalDriver();
        try {
            wait.until(webDriver -> element.isDisplayed());
        } catch (org.openqa.selenium.StaleElementReferenceException e) {
            wait.until(webDriver -> element.isDisplayed());
        }
    }

    public static void implicitWait(int time) {
        DriverFactory.getDriver().manage().timeouts().implicitlyWait(time, TimeUnit.SECONDS);
    }

    /**
     * Waiting 30 seconds for an element to be present on the page, checking
     * for its presence once every 5 seconds.
     */
    public static void waitForElementToBePresent(WebElement element) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public static void waitForElementToBePresentByBody(WebElement element) {
        //setLocalDriver();
        wait.until(webDriver -> ExpectedConditions.visibilityOf(element));
    }

    public static String fetchClipboardContents() throws IOException, UnsupportedFlavorException {
        System.setProperty("java.awt.headless", "false");
        if (System.getProperty("os.name").startsWith("Windows")) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            return clipboard.getData(DataFlavor.stringFlavor).toString();
        } else {
            String contents = "";
            String command = "xclip --clipboard -o";
            Process process = Runtime.getRuntime().exec(command);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                contents = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return contents;
        }
    }

    public static void waitForElementToBePresent(String element) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(30));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(element)));
    }

    public static void waitForElementRefreshed(WebElement element) {
        wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOf(element)));
    }

    public static void ScrollToBottom() {

        JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();

        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

        //Uninterruptibles.sleepUninterruptibly(1500, TimeUnit.MILLISECONDS);
    }

    public static void waitElementToBeClickeable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public static WebElement findFirstElementChild(WebElement element) {
        WebElement siblingElement = (WebElement) ((JavascriptExecutor) DriverFactory.getDriver()).executeScript(
                "return arguments[0].firstElementChild;", element);
        return siblingElement;
    }

    public static boolean isElementPresent(final WebElement element) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(15));
        try {
            return wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    if (element.isDisplayed()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("The wait timed out, couldnt not find element");
            return false;
        }
    }

    public static boolean isElementPresentWithAttribute(WebElement element, String attribute, String value) {
        return new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(20)).until(ExpectedConditions.attributeContains(element, attribute, value));
    }

    public static boolean isAttributePresent(WebElement element, String attribute) {
        boolean result = false;
        try {
            String value = element.getAttribute(attribute);
            if (value != null) {
                result = true;
            }
        } catch (Exception e) {
        }
        return result;
    }

    public static void pageLoadTimeout() {
        new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(15)).until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
    }

    public static void waitPageLoad() {
        DriverFactory.getDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
    }

    public static boolean getPageReady(By element) throws InterruptedException {
        int count = 0;
        int maxTries = 2;
        while (count < 2) {
            try {
                //setLocalDriver();
                wait.until(webDriver -> ExpectedConditions.presenceOfElementLocated(element));
                return true;
            } catch (NoSuchElementException | TimeoutException | NullPointerException e) {
                // handle exception
                if (++count == maxTries) throw e;
            }
        }
        return false;
    }

    public static void waitForElementToNotNull(WebElement element) {
        (new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(10))).until((ExpectedCondition<Boolean>) d -> {
            return element.getText().length() != 0;
        });
    }

    public static void waitForText(WebElement element, String expectedText, Duration timeoutInSeconds, int maxRetries) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), timeoutInSeconds);
        for (int retry = 0; retry < maxRetries; retry++) {
            try {
                wait.until((ExpectedCondition<Boolean>) driver -> {
                    String actualText = element.getText();
                    return actualText.contains(expectedText);
                });
                return;  // Text found, no need to retry
            } catch (TimeoutException e) {
                if (retry < maxRetries - 1) {
                    System.out.println("Text not found. Retrying...");
                } else {
                    throw e;
                }
            }
        }
    }

    public static void refreshPage() {
        DriverFactory.getDriver().navigate().refresh();
    }

    /**
     * Waits until the specified attribute of a web element matches the expected value.
     *
     * @param element       The web element whose attribute you want to check.
     * @param attributeName The name of the attribute to be checked.
     * @param expectedValue The expected value of the attribute.
     */
    public static void waitForAttributeToBe(WebElement element, String attributeName, String expectedValue, Duration timeoutInSeconds, int maxRetries) {
        for (int i = 0; i <= maxRetries; i++) {
            try {
                WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), timeoutInSeconds);
                wait.until(ExpectedConditions.attributeToBe(element, attributeName, expectedValue));
                // If the expected attribute value is reached, exit the loop
                break;
            } catch (TimeoutException e) {
                System.out.println("Retry #" + (i + 1) + ": Timeout while waiting for attribute change...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getTextByJsExecutor(WebElement element) {
        return (String) ((JavascriptExecutor) DriverFactory.getDriver()).executeScript("return arguments[0].textContent;", element);
    }

    public static WebElement reFindElementWithRetry(By locator, int attempts) {
        WebElement element = null;
        for (int i = 0; i < attempts; i++) {
            try {
                element = DriverFactory.getDriver().findElement(locator);
                return element; // Return if found
            } catch (Exception e) {
                System.out.println("Attempt " + (i + 1) + ": Retrying...");
            }
        }
        throw new RuntimeException("Element not found after " + attempts + " attempts");
    }

    public static String getElementTextByXPath(WebDriver driver, String xpath) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
            String text = (String) js.executeScript(
                    "return document.evaluate(arguments[0], document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.textContent;",
                    xpath
            );
            return text;
        } catch (Exception e) {
            System.err.println("Error retrieving text for XPath: " + xpath);
            e.printStackTrace();
            return null; // Return null if the element is not found or another error occurs
        }
    }

    /**
     * Set Keys via SendKeys
     *
     * @param element:Element to set
     */
    public static void setEnterKeys(WebElement element) {
        element.sendKeys(Keys.ENTER);
    }

    /**
     * Set Keys via SendKeys
     *
     * @param element:Element to set
     */
    public static void deleteUsingKeys(WebElement element) {
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(Keys.DELETE);
    }

    public static boolean isElementVisibleJS(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
        return (Boolean) js.executeScript(
                "var e = arguments[0];" +
                        "if (!e) return false;" +
                        "var style = window.getComputedStyle(e);" +
                        "return style.display !== 'none' && style.visibility !== 'hidden' && style.opacity !== '0';",
                element
        );
    }

    public static void doubleClickForIOS(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        if (element instanceof WrapsElement wrapsElement) {
            element = wrapsElement.getWrappedElement();
        }
        String elementId = ((RemoteWebElement) element).getId();

        Map<String, Object> args = new HashMap<>();
        args.put("element", elementId);

        ((JavascriptExecutor) DriverFactory.getDriver()).executeScript("mobile: doubleTap", args);
    }

    public static void scrollDownToTextAndClickForAndroid(String text) {
        DriverFactory.getDriver().findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                        ".scrollIntoView(new UiSelector().textContains(\"" + text + "\"))"
        )).click();
    }

    public static WebElement scrollDownToTextForAndroid(String text) {
        try {
            return DriverFactory.getDriver().findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().textContains(\"" + text + "\"))"
            ));
        } catch (Exception e) {
            System.out.println("Could not scroll to text: " + text);
            return null;
        }
    }

    public static WebElement scrollDownToTextForIOS(String text) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("predicateString", "label CONTAINS[c] '" + text + "'");
            params.put("toVisible", "true");

            AppiumDriver driver = (AppiumDriver) DriverFactory.getDriver();
            driver.executeScript("mobile: scroll", params);

            return DriverFactory.getDriver().findElement(AppiumBy.iOSNsPredicateString("label CONTAINS[c] '" + text + "'"));
        } catch (Exception e) {
            System.out.println("Could not scroll to text (iOS): " + text);
            return null;
        }
    }

    public static void scrollDownToElementAndClickForIOS(By locator) {
        int maxSwipes = 6;
        AppiumDriver driver = (AppiumDriver) DriverFactory.getDriver();

        for (int i = 0; i < maxSwipes; i++) {
            java.util.List<WebElement> elem = DriverFactory.getDriver().findElements(locator);
            for (WebElement el : elem) {
                if (el.isDisplayed()) {
                    el.click();
                    return;
                }
            }
            swipeUp(driver);
        }

        System.out.println("Element not found after max swipes.");

    }

    public static void scrollDownToElementForIOS(By locator) {
        int maxSwipes = 6;
        AppiumDriver driver = (AppiumDriver) DriverFactory.getDriver();

        for (int i = 0; i < maxSwipes; i++) {
            java.util.List<WebElement> elem = DriverFactory.getDriver().findElements(locator);
            for (WebElement el : elem) {
                if (el.isDisplayed()) {
                    return;
                }
            }
            swipeUp(driver);
        }

        System.out.println("Element not found after max swipes.");

    }

    public static void swipeUp(AppiumDriver driver) {
        Map<String, Object> swipeParams = new HashMap<>();
        swipeParams.put("direction", "up");
        swipeParams.put("velocity", 1000);
        driver.executeScript("mobile: swipe", swipeParams);
    }


    public static void waitForTextToBePresent(WebElement element, String expectedString) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.textToBePresentInElement(
                element,
                expectedString
        ));
    }

    public static void setTextAndroid(WebElement element, String text) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOf(element));
        element.click();
        element.clear();
        element.sendKeys(text);
    }

    public static void clickElementMobile(WebElement element) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOf(element));
        element.click();
    }

    public static void setTextForIOS(WebElement element, String text) {
        element.click();
        ((IOSDriver) DriverFactory.getDriver()).setClipboardText(text);
        element.sendKeys(Keys.chord(Keys.COMMAND, "v"));
        element.clear();
        element.sendKeys(text);
    }

    public static void scrollDownIOS() {
        AppiumDriver driver = (AppiumDriver) DriverFactory.getDriver();

        Dimension size = DriverFactory.getDriver().manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.2);

        Map<String, Object> swipeArgs = new HashMap<>();
        swipeArgs.put("direction", "up");
        swipeArgs.put("velocity", 2500);
        swipeArgs.put("startX", startX);
        swipeArgs.put("startY", startY);
        swipeArgs.put("endX", startX);
        swipeArgs.put("endY", endY);

        driver.executeScript("mobile: swipe", swipeArgs);
    }


}
