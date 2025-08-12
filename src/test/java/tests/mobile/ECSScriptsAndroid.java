package tests.mobile;

import framework.base.PageObjectInitiator;
import framework.base.TestBase;
import framework.utils.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

public class ECSScriptsAndroid extends TestBase {

    @Test(description = "Verify mobile login using valid credentials", dataProvider = "commonDataProvider", dataProviderClass = DataProvider.class)
    public void TC01_Android_logInToECSWithValidID(Map<String, String> data) {
        System.out.println("Started Test");
        setTestData(data);

        PageObjectInitiator.getObjectAndroidLoginPage().validateLogInPageDisplayed();
        PageObjectInitiator.getObjectAndroidLoginPage().loginToECSAndroidApp();
    }
}
