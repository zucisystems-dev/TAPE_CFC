package app.web.ECS;

import framework.utils.WebActions;
import com.aventstack.extentreports.Status;
import org.junit.Assert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static framework.utils.ScreenShotUtil.captureScreenshot;

public class ECSHomePage extends WebActions{

    public WebDriver driver;

    public ECSHomePage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//a[contains(text(),'HOME')]")
    WebElement linkHome;

    @FindBy(xpath = "//a[contains(text(),'PAYMENTS')]")
    WebElement linkPayments;

    @FindBy(xpath = "//a[contains(text(),'STATEMENTS')]")
    WebElement linkStatements;

    @FindBy(xpath = "//a[contains(text(),'ALERTS & NOTIFICATIONS')]")
    WebElement linkAlertsAndNotifications;

    @FindBy(xpath = "//a[contains(text(),'CARD SERVICES')]")
    WebElement linkCardServices;

    @FindBy(id = "homeMainButtonMinPaymentDue")
    WebElement btnMinimumPaymentDue;

    @FindBy(id = "homeMainButtonPaymentDueDate")
    WebElement btnPaymentDueDate;

    @FindBy(id = "homeMainButtonLastStmtBalance")
    WebElement btnLastStatementBalance;

    @FindBy(id = "homeMainButtonRecentActivity")
    WebElement btnRecentActivity;

    @FindBy(xpath = "//a[contains(text(),'MAKE A PAYMENT')]")
    WebElement linkMakeAPayment;

    public void clickMakeAPayment() {
            clickElement(linkPayments, "Payments link is clicked");
            waitTillElementVisible(linkMakeAPayment);
            clickElement(linkMakeAPayment, "Make A Payment link is clicked");
    }

    public void clickMinimumPaymentDue() {
        clickElement(btnMinimumPaymentDue, "Minimum Payment Due button is clicked");
    }

    public void clickPaymentDueDate() {
        clickElement(btnPaymentDueDate, "Payment Due Date button is clicked");
    }

    public void clickStatementBalance() {
        clickElement(btnLastStatementBalance, "Statement Balance button is clicked");
    }

    public void validateHomePageDisplayed(){
        try {
            waitTillElementVisible(linkPayments);
            if (elementIsDisplayed(linkHome) && elementIsDisplayed(btnMinimumPaymentDue)) {
                captureScreenshot(Status.PASS, "ECS Home Page is displayed");
            } else {
                captureScreenshot(Status.FAIL, "ECS Home Page is not displayed");
            }
        }
        catch(TimeoutException e){
            Assert.fail("Home Page not Displayed");
        }
    }

}
