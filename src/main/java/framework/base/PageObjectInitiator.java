package framework.base;


import app.web.ECS.*;
import org.openqa.selenium.WebDriver;

public class PageObjectInitiator {

    static ThreadLocal<ECSLoginPage> ECS_LOGIN_PAGE = new ThreadLocal<ECSLoginPage>();
    static ThreadLocal<ECSHomePage> ECS_HOME_PAGE = new ThreadLocal<ECSHomePage>();
    static ThreadLocal<ECSMakePaymentPage> ECS_MAKEPAYMENT_PAGE = new ThreadLocal<ECSMakePaymentPage>();
    static ThreadLocal<ECSAutoPayPage> ECS_AUTOPAY_PAGE = new ThreadLocal<ECSAutoPayPage>();
    static ThreadLocal<ECSStatementsPage> ECS_STATEMENTS_PAGE = new ThreadLocal<ECSStatementsPage>();

    public static void objectInitiator(WebDriver driver){
        ECS_LOGIN_PAGE.set(new ECSLoginPage(driver));
        ECS_HOME_PAGE.set(new ECSHomePage(driver));
        ECS_MAKEPAYMENT_PAGE.set(new ECSMakePaymentPage(driver));
        ECS_AUTOPAY_PAGE.set(new ECSAutoPayPage(driver));
        ECS_STATEMENTS_PAGE.set(new ECSStatementsPage(driver));
    }

    public static ECSLoginPage getObjectLoginPage(){
        return ECS_LOGIN_PAGE.get();
    }

    public static ECSHomePage getObjectHomePage(){
        return ECS_HOME_PAGE.get();
    }

    public static ECSMakePaymentPage getObjectMakePaymentPage(){
        return ECS_MAKEPAYMENT_PAGE.get();
    }

    public static ECSAutoPayPage getObjectAutoPayPage(){
        return ECS_AUTOPAY_PAGE.get();
    }

    public static ECSStatementsPage getObjectStatementsPage(){
        return ECS_STATEMENTS_PAGE.get();
    }

}
