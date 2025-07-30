package app.web.ECS;

import framework.utils.WebActions;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static framework.utils.ScreenShotUtil.captureScreenshot;

public class ECSMakePaymentPage extends WebActions {

    public WebDriver driver;

    public ECSMakePaymentPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(id = "optionPaymentAmount")
    WebElement btnPaymentAmount;

    @FindBy(id = "optionPaymentDate")
    WebElement btnPaymentDate;

    @FindBy(id = "optionPaymentMethod")
    WebElement btnPaymentMethod;

    @FindBy(xpath = "//button[normalize-space() = 'MAKE A PAYMENT']")
    WebElement btnMakeAPayment;

    @FindBy(xpath = "//button[normalize-space() = 'CANCEL']")
    WebElement btnCancel;

    public void validateMakePaymentPageDisplayed(){
            waitTillElementVisible(btnPaymentAmount);
            if(elementIsDisplayed(btnPaymentAmount) && elementIsDisplayed(btnMakeAPayment)){
                captureScreenshot(Status.PASS,"ECS Make A Payment Page is displayed");
            }
            else{
                captureScreenshot(Status.FAIL,"ECS Make A Payment is not displayed");
            }
    }

}
