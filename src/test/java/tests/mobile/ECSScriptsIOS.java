package tests.mobile;

import framework.base.PageObjectInitiator;
import framework.base.TestBase;
import framework.utils.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

public class ECSScriptsIOS extends TestBase {

    @Test(description = "Verify ios login using valid credentials", dataProvider = "commonDataProvider", dataProviderClass = DataProvider.class)
    public void TC01_IOS_logInToECSWithValidID(Map<String, String> data) {
        System.out.println("Started Test");
        setTestData(data);

        PageObjectInitiator.getObjectLoginIOSPage().loginToECSIOSApp();
    }
}
