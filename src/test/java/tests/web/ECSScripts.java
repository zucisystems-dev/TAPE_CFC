package tests.web;

import framework.base.PageObjectInitiator;
import framework.base.TestBase;
import framework.utils.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

public class ECSScripts extends TestBase {

    @Test(description = "Verify login using valid credentials", dataProvider = "ecsDataProvider", dataProviderClass = DataProvider.class)
    public void TC01_logInToECSWithValidID(Map<String, String> data) {
        setTestData(data);

        PageObjectInitiator.getObjectLoginPage().handleProceedToLogin();
        PageObjectInitiator.getObjectLoginPage().validateLogInPageDisplayed();
        PageObjectInitiator.getObjectLoginPage().logInToApplication();
        PageObjectInitiator.getObjectHomePage().validateHomePageDisplayed();
    }


    @Test(description = "Verify login using Invalid credentials", dataProvider = "ecsDataProvider", dataProviderClass = DataProvider.class)
    public void TC02_logInToECSWithInvalidID(Map<String, String> data) {
        setTestData(data);

        PageObjectInitiator.getObjectLoginPage().handleProceedToLogin();
        PageObjectInitiator.getObjectLoginPage().validateLogInPageDisplayed();
        PageObjectInitiator.getObjectLoginPage().logInToApplication();
        PageObjectInitiator.getObjectLoginPage().validateInvalidCredentialsMessage();
    }

    @Test(description = "Verify if user is able to navigate to Make A Payment page", dataProvider = "ecsDataProvider", dataProviderClass = DataProvider.class)
    public void TC03_navigateToMakeAPaymentPage(Map<String, String> data) {
        setTestData(data);

        PageObjectInitiator.getObjectLoginPage().handleProceedToLogin();
        PageObjectInitiator.getObjectLoginPage().validateLogInPageDisplayed();
        PageObjectInitiator.getObjectLoginPage().logInToApplication();
        PageObjectInitiator.getObjectHomePage().validateHomePageDisplayed();
        PageObjectInitiator.getObjectHomePage().clickMakeAPayment();
        PageObjectInitiator.getObjectMakePaymentPage().validateMakePaymentPageDisplayed();
    }

    @Test(description = "Verify if user is able to navigate to Auto Pay page", dataProvider = "ecsDataProvider", dataProviderClass = DataProvider.class)
    public void TC04_navigateToAutoPayPage(Map<String, String> data) {
        setTestData(data);

        PageObjectInitiator.getObjectLoginPage().handleProceedToLogin();
        PageObjectInitiator.getObjectLoginPage().validateLogInPageDisplayed();
        PageObjectInitiator.getObjectLoginPage().logInToApplication();
        PageObjectInitiator.getObjectHomePage().validateHomePageDisplayed();
        PageObjectInitiator.getObjectHomePage().clickPaymentDueDate();
        PageObjectInitiator.getObjectAutoPayPage().validateAutoPayPageDisplayed();
    }

    @Test(description = "Verify if user is able to navigate to Auto Pay page", dataProvider = "ecsDataProvider", dataProviderClass = DataProvider.class)
    public void TC05_navigateToStatementsPage(Map<String, String> data) {
        setTestData(data);

        PageObjectInitiator.getObjectLoginPage().handleProceedToLogin();
        PageObjectInitiator.getObjectLoginPage().validateLogInPageDisplayed();
        PageObjectInitiator.getObjectLoginPage().logInToApplication();
        PageObjectInitiator.getObjectHomePage().validateHomePageDisplayed();
        PageObjectInitiator.getObjectHomePage().clickStatementBalance();
        PageObjectInitiator.getObjectStatementsPage().validateStatementsPageDisplayed();
    }

}

