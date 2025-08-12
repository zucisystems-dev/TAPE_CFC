package tests.web;

import framework.base.DriverFactory;
import framework.base.PageObjectInitiator;
import framework.base.TestBase;
import framework.config.PropertyReader;
import framework.utils.DataProvider;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import java.util.Map;

import static framework.utils.WebActions.newDriverInstance;
import static framework.utils.WebActions.openNewApplication;

public class ECSScripts extends TestBase {

    @Test(description = "Verify login using valid credentials", dataProvider = "commonDataProvider", dataProviderClass = DataProvider.class)
    public void TC01_logInToECSWithValidID(Map<String, String> data, ITestContext context) {
        System.out.println("Started Test");
        setTestData(data);

        PageObjectInitiator.getObjectLoginPage().handleProceedToLogin();
        PageObjectInitiator.getObjectLoginPage().validateLogInPageDisplayed();
        PageObjectInitiator.getObjectLoginPage().logInToApplication();
        PageObjectInitiator.getObjectHomePage().validateHomePageDisplayed();
        openNewApplication(PropertyReader.getPropertyFileURL("CSP",
                context.getCurrentXmlTest().getParameter("env").split("_")[1]), DriverFactory.getDriver());
        PageObjectInitiator.getObjectLoginPage().handleProceedToLogin();
        PageObjectInitiator.getObjectLoginPage().validateLogInPageDisplayed();
        PageObjectInitiator.getObjectLoginPage().logInToApplication();
        PageObjectInitiator.getObjectHomePage().validateHomePageDisplayed();
        PageObjectInitiator.getObjectHomePage().clickMakeAPayment();
    }


    @Test(description = "Verify login using Invalid credentials", dataProvider = "commonDataProvider", dataProviderClass = DataProvider.class)
    public void TC02_logInToECSWithInvalidID(Map<String, String> data) {
        System.out.println("Started Test");
        setTestData(data);

        PageObjectInitiator.getObjectLoginPage().handleProceedToLogin();
        PageObjectInitiator.getObjectLoginPage().validateLogInPageDisplayed();
        PageObjectInitiator.getObjectLoginPage().logInToApplication();
        PageObjectInitiator.getObjectLoginPage().validateInvalidCredentialsMessage();
    }

    @Test(description = "Verify if user is able to navigate to Make A Payment page", dataProvider = "commonDataProvider", dataProviderClass = DataProvider.class)
    public void TC03_navigateToMakeAPaymentPage(Map<String, String> data, ITestContext context) {
        System.out.println("Started Test");
        setTestData(data);

        PageObjectInitiator.getObjectLoginPage().handleProceedToLogin();
        PageObjectInitiator.getObjectLoginPage().validateLogInPageDisplayed();
        PageObjectInitiator.getObjectLoginPage().logInToApplication();
        PageObjectInitiator.getObjectHomePage().validateHomePageDisplayed();
        PageObjectInitiator.getObjectHomePage().clickMakeAPayment();
        PageObjectInitiator.getObjectMakePaymentPage().validateMakePaymentPageDisplayed();
        openNewApplication(PropertyReader.getPropertyFileURL("CSP",
                context.getCurrentXmlTest().getParameter("env").split("_")[1]), DriverFactory.getDriver());
        PageObjectInitiator.getObjectLoginPage().handleProceedToLogin();
        PageObjectInitiator.getObjectLoginPage().validateLogInPageDisplayed();
        PageObjectInitiator.getObjectLoginPage().logInToApplication();
        PageObjectInitiator.getObjectHomePage().validateHomePageDisplayed();
        PageObjectInitiator.getObjectHomePage().clickMakeAPayment();
    }

    @Test(description = "Verify if user is able to navigate to Auto Pay page", dataProvider = "commonDataProvider", dataProviderClass = DataProvider.class)
    public void TC04_navigateToAutoPayPage(Map<String, String> data) {
        System.out.println("Started Test");
        setTestData(data);

        PageObjectInitiator.getObjectLoginPage().handleProceedToLogin();
        PageObjectInitiator.getObjectLoginPage().validateLogInPageDisplayed();
        PageObjectInitiator.getObjectLoginPage().logInToApplication();
        PageObjectInitiator.getObjectHomePage().validateHomePageDisplayed();
        PageObjectInitiator.getObjectHomePage().clickPaymentDueDate();
        PageObjectInitiator.getObjectAutoPayPage().validateAutoPayPageDisplayed();
        PageObjectInitiator.getObjectLoginPage().validateLogInPageDisplayed();
    }

    @Test(description = "Verify if user is able to navigate to Auto Pay page", dataProvider = "commonDataProvider", dataProviderClass = DataProvider.class)
    public void TC05_navigateToStatementsPage(Map<String, String> data, ITestContext context) {
        System.out.println("Started Test");
        setTestData(data);

        PageObjectInitiator.getObjectLoginPage().handleProceedToLogin();
        PageObjectInitiator.getObjectLoginPage().validateLogInPageDisplayed();
        PageObjectInitiator.getObjectLoginPage().logInToApplication();
        PageObjectInitiator.getObjectHomePage().validateHomePageDisplayed();
        PageObjectInitiator.getObjectHomePage().clickStatementBalance();
        PageObjectInitiator.getObjectStatementsPage().validateStatementsPageDisplayed();
        openNewApplication(PropertyReader.getPropertyFileURL("CSP",
                context.getCurrentXmlTest().getParameter("env").split("_")[1]), DriverFactory.getDriver());
        PageObjectInitiator.getObjectLoginPage().handleProceedToLogin();
        PageObjectInitiator.getObjectLoginPage().validateLogInPageDisplayed();
        PageObjectInitiator.getObjectLoginPage().logInToApplication();
        PageObjectInitiator.getObjectHomePage().validateHomePageDisplayed();
        PageObjectInitiator.getObjectHomePage().clickMakeAPayment();
    }

}

