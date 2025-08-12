package app.web.ECS;

import com.aventstack.extentreports.Status;
import framework.utils.WebActions;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static framework.utils.ScreenShotUtil.captureScreenshot;

public class ECSLoginPageAndroid extends WebActions {

    public WebDriver driver;

    @FindBy(xpath = "//android.widget.EditText[@content-desc=\"Username\"]")
    WebElement boxUserName;

    @FindBy(xpath = "//android.widget.EditText[@content-desc=\"Password\"]")
    WebElement boxPassword;

    @FindBy(xpath = "//android.widget.TextView[@text=\"LOGIN\"]")
    WebElement btnLogIn;

    @FindBy(xpath = "//android.widget.Button[@content-desc=\"PROCEED TO LOGIN\"]")
    WebElement btnProceedLogin;

    @FindBy(xpath = "//android.widget.ImageView[@resource-id=\"HOME_CREDIT_CARD-0-image\"]")
    WebElement homePage;

    public ECSLoginPageAndroid(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void setUsername(String userName){
        setTextAndroid(boxUserName, userName);
        captureScreenshot(Status.PASS,"Username is entered");
    }

    public void setPassword(String password){
        setTextAndroid(boxPassword, password);
        captureScreenshot(Status.PASS,"password is entered");
    }

    public void validateLogInPageDisplayed(){
            waitTillElementVisible(boxUserName);
            if(elementIsDisplayed(boxUserName) && elementIsDisplayed(boxPassword)){
                captureScreenshot(Status.PASS,"ECS LogIn Page is displayed");
            }
            else{
                captureScreenshot(Status.FAIL,"ECS LogIn Page is not displayed");
            }
    }

    public void loginToECSAndroidApp(){
        try {
            if(WebActions.isElementPresent(btnProceedLogin)) {
                scrolltoElement(btnProceedLogin);
                waitTillElementClickable(btnProceedLogin);
                clickElement(btnProceedLogin, "Proceed To Login button is clicked");
                System.out.println("Optional element appeared and was clicked.");
            }
            setUsername(getTestData().get("username"));
            setPassword(getTestData().get("password"));
            scrolltoElement(boxPassword);
            waitTillElementClickable(btnLogIn);
            captureScreenshot(Status.PASS,"Username and Password is entered");
            clickElement(btnLogIn, "Login Button is Clicked");
            waitTillElementClickable(homePage);
            captureScreenshot(Status.PASS,"Login is Success");
        } catch (TimeoutException e) {
            captureScreenshot(Status.FAIL,"Login is Failed");
        }
    }
}
