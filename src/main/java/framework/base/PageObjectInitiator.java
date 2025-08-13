package framework.base;


import app.mobile.ECS.ECSLoginPageAndroid;
import app.mobile.ECS.ECSLoginPageIOS;
import app.web.ECS.*;
import framework.api.RestApi;
import org.openqa.selenium.WebDriver;

public class PageObjectInitiator {

    static ThreadLocal<ECSLoginPage> ECS_LOGIN_PAGE = new ThreadLocal<ECSLoginPage>();
    static ThreadLocal<ECSHomePage> ECS_HOME_PAGE = new ThreadLocal<ECSHomePage>();
    static ThreadLocal<ECSMakePaymentPage> ECS_MAKEPAYMENT_PAGE = new ThreadLocal<ECSMakePaymentPage>();
    static ThreadLocal<ECSAutoPayPage> ECS_AUTOPAY_PAGE = new ThreadLocal<ECSAutoPayPage>();
    static ThreadLocal<ECSStatementsPage> ECS_STATEMENTS_PAGE = new ThreadLocal<ECSStatementsPage>();
    static ThreadLocal<RestApi> REST_API = new ThreadLocal<>();
    static ThreadLocal<ECSLoginPageAndroid> ECS_LOGIN_ANDROID_PAGE = new ThreadLocal<>();
    static ThreadLocal<ECSLoginPageIOS> ECS_LOGIN_IOS_PAGE = new ThreadLocal<>();

    private PageObjectInitiator(){
        throw new UnsupportedOperationException("Object Initiator class — do not instantiate.");
    }

    public static void objectInitiator(WebDriver driver){
        ECS_LOGIN_PAGE.set(new ECSLoginPage(driver));
        ECS_HOME_PAGE.set(new ECSHomePage(driver));
        ECS_MAKEPAYMENT_PAGE.set(new ECSMakePaymentPage(driver));
        ECS_AUTOPAY_PAGE.set(new ECSAutoPayPage(driver));
        ECS_STATEMENTS_PAGE.set(new ECSStatementsPage(driver));
        ECS_LOGIN_ANDROID_PAGE.set(new ECSLoginPageAndroid(driver));
        ECS_LOGIN_IOS_PAGE.set(new ECSLoginPageIOS(driver));
    }

    public static void apiObjectInitiator(){
        REST_API.set(new RestApi());
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

    public static ECSLoginPageAndroid getObjectAndroidLoginPage(){
        return ECS_LOGIN_ANDROID_PAGE.get();
    }

    public static ECSLoginPageIOS getObjectLoginIOSPage(){
        return ECS_LOGIN_IOS_PAGE.get();
    }

    public static RestApi getObjectRestApi(){
        return REST_API.get();
    }

}
