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

public class ECSAutoPayPage extends WebActions {

    public WebDriver driver;

    public ECSAutoPayPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//h1[contains(text(),'Set Up Auto Pay')]")
    WebElement titleSetUpAutoPay;

    @FindBy(id = "paymentAmount")
    WebElement btnPaymentAmount;

    @FindBy(id = "paymentMethod")
    WebElement btnPaymentMethod;

    @FindBy(xpath = "//button[normalize-space() = 'SAVE AUTO PAY']")
    WebElement btnSaveAutoPay;

    @FindBy(xpath = "//button[normalize-space() = 'CANCEL']")
    WebElement btnCancel;

    public void validateAutoPayPageDisplayed(){
        try {
            waitTillElementVisible(titleSetUpAutoPay);
            if (elementIsDisplayed(titleSetUpAutoPay)) {
                captureScreenshot(Status.PASS, "ECS Auto Pay Page is displayed");
            } else {
                captureScreenshot(Status.FAIL, "ECS Auto Pay Page is not displayed");
            }
        }
        catch(TimeoutException e){
            Assert.fail("Auto Pay Page not Displayed");
        }
    }

}
