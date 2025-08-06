package app.web.ECS;

import framework.utils.WebActions;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static framework.utils.ScreenShotUtil.captureScreenshot;

public class ECSLoginPage extends WebActions {

    public WebDriver driver;

    @FindBy(id = "formBasicEmail")
    WebElement boxUserName;

    @FindBy(id = "formBasicPassword")
    WebElement boxPassword;

    @FindBy(xpath = "//button[@data-ui='login-btn-login']")
    WebElement btnLogIn;

    @FindBy(id = "idLnkFeedback")
    WebElement linkProvideFeedBack;

    @FindBy(xpath = "//a[contains(text(),'Forgot Username?')]")
    WebElement linkForgotUsername;

    @FindBy(xpath = "//a[contains(text(),'Forgot Password?')]")
    WebElement linkForgotPassword;

    @FindBy(id = "//button[@data-ui='login-anchor-register']")
    WebElement linkSignUp;

    @FindBy(xpath = "//a[contains(text(),'Activate a New Card')]")
    WebElement linkActivateNewCard;

    @FindBy(xpath = "//p[contains(text(),'Invalid Username or Password.')]")
    WebElement titleInvalidCredentials;

    @FindBy(xpath = "//button[normalize-space() = 'PROCEED TO LOGIN']")
    WebElement btnProceedLogin;

    public ECSLoginPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickProvideFeedBack() {
            clickElement(linkProvideFeedBack, "Provide Feedback link is clicked");
    }

    public void clickForgotUserName() {
            clickElement(linkForgotUsername, "Forgot UserName link is Clicked");
    }

    public void clickSignUp() {
            clickElement(linkSignUp, "Sign Up link is clicked");
    }

    public void clickForgotPassword() {
            clickElement(linkForgotPassword, "Forgot Password link is clicked");
    }

    public void clickActivateNewCard() {
            clickElement(linkActivateNewCard, "Activate New Card link is clicked");
    }

    public void logInToApplication() {
        setText(boxUserName, getTestData().get("username"), "Username is entered");
        setText(boxPassword, getTestData().get("password"),"Password is entered");
        scrolltoElement(boxPassword);
        waitTillElementClickable(btnLogIn);
        captureScreenshot(Status.PASS,"Username and Password is entered");
        clickElement(btnLogIn, "Login Button is Clicked");
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

    public void validateInvalidCredentialsMessage(){
        waitTillElementVisible(titleInvalidCredentials);
        if(elementIsDisplayed(titleInvalidCredentials)){
            captureScreenshot(Status.PASS,"Invalid Username or Password message is displayed");
        }
        else{
            captureScreenshot(Status.FAIL,"Invalid Username or Password message is not displayed");
        }
    }

    public void handleProceedToLogin(){
        try {
            waitTillElementVisible(btnProceedLogin);
            if(btnProceedLogin.isDisplayed()) {
                scrolltoElement(btnProceedLogin);
                waitTillElementClickable(btnProceedLogin);
                clickElement(btnProceedLogin, "Proceed To Login button is clicked");
                System.out.println("Optional element appeared and was clicked.");
            }
        } catch (TimeoutException e) {
            System.out.println("Optional element did not appear.");
        }
    }
}
