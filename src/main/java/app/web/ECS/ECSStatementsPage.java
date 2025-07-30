package app.web.ECS;

import framework.utils.WebActions;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static framework.utils.ScreenShotUtil.captureScreenshot;

public class ECSStatementsPage extends WebActions {

    public WebDriver driver;

    public ECSStatementsPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//h1[contains(text(),'Statements')]")
    WebElement titleStatements;

    @FindBy(xpath = "//div[@class='DropDownMenu show dropdown']//button")
    WebElement btnStatementYear;

    public void validateStatementsPageDisplayed(){
        waitTillElementVisible(titleStatements);
        if(elementIsDisplayed(titleStatements)){
            captureScreenshot(Status.PASS,"ECS Statements Page is displayed");
        }
        else{
            captureScreenshot(Status.FAIL,"ECS Statements Page is not displayed");
        }
    }


}
