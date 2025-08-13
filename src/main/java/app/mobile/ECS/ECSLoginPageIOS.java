package app.mobile.ECS;

import com.aventstack.extentreports.Status;
import framework.utils.WebActions;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static framework.utils.ScreenShotUtil.captureScreenshot;

public class ECSLoginPageIOS extends WebActions {

    public WebDriver driver;

    @FindBy(xpath = "//XCUIElementTypeTextField[@name=\"INPUT_NAME\"]")
    WebElement boxUserName;

    @FindBy(xpath = "//XCUIElementTypeSecureTextField[@name=\"INPUT_PASSWORD\"]")
    WebElement boxPassword;

    @FindBy(xpath = "//XCUIElementTypeButton[@name=\"BUTTON_LOGIN\"]")
    WebElement btnLogIn;

    @FindBy(xpath = "//XCUIElementTypeButton[@name=\"PROCEED TO LOGIN\"]")
    WebElement btnProceedLogin;

    @FindBy(xpath = "//XCUIElementTypeOther[@name=\"HOME_CREDIT_CARD-0-image\"]")
    WebElement homePage;

    @FindBy(xpath = "//XCUIElementTypeButton[@name=\"Don’t Allow\"]")
    WebElement dontAllowBtn;

    @FindBy(xpath = "//XCUIElementTypeButton[@name=\"View password button\"]")
    WebElement viewPasswordBtn;

    public ECSLoginPageIOS(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void setUsername(String userName){
        waitTillElementVisible(boxUserName);
        boxUserName.sendKeys(userName);
        captureScreenshot(Status.PASS,"Username is entered");
    }

    public void setPassword(String password){
        setTextForIOS(boxPassword, password);
        captureScreenshot(Status.PASS,"password is entered");
    }

    public void loginToECSIOSApp(){
        try {
            if(WebActions.isElementPresent(btnProceedLogin)) {
                scrolltoElement(btnProceedLogin);
                waitTillElementClickable(btnProceedLogin);
                clickElement(btnProceedLogin, "Proceed To Login button is clicked");
                System.out.println("Optional element appeared and was clicked.");
            }
            if(WebActions.isElementPresent(dontAllowBtn)) {
                clickElementMobile(dontAllowBtn);
            }
            setUsername(getTestData().get("username"));
            setPassword(getTestData().get("password"));
            waitTillElementClickable(btnLogIn);
            doubleClickForIOS(viewPasswordBtn);
            captureScreenshot(Status.PASS,"Username and Password is entered");
            clickElement(btnLogIn, "Login Button is Clicked");
            waitTillElementClickable(homePage);
            captureScreenshot(Status.PASS,"Login is Success");
        } catch (TimeoutException e) {
            captureScreenshot(Status.FAIL,"Login is Failed");
        }
    }
}
