package tests.api;

import framework.base.PageObjectInitiator;
import framework.base.TestBase;
import framework.utils.DataProvider;
import io.restassured.response.Response;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import java.util.Map;


public class SampleAPI extends TestBase {

    @Test(description = "Verify Get Data for Sample API")
    public void TC01_GetSomeData(ITestContext context) {

        Response response = PageObjectInitiator.getObjectRestApi().restApiGetRequest(context);
        PageObjectInitiator.getObjectRestApi().verifyResponseCode(200, response);
        PageObjectInitiator.getObjectRestApi().logResponseInReport(200, response);
    }

    @Test(description = "Verify Post Data for Sample API", dataProvider = "commonDataProvider", dataProviderClass = DataProvider.class)
    public void TC02_PostSomeData(Map<String, String> data, ITestContext context) {
        setTestData(data);

        Response response = PageObjectInitiator.getObjectRestApi().restApiPostRequest(context);
        PageObjectInitiator.getObjectRestApi().verifyResponseCode(201, response);
        PageObjectInitiator.getObjectRestApi().validateResponseBodyContains(response,getTestData().get("name"));
        PageObjectInitiator.getObjectRestApi().validateResponseBodyContains(response,getTestData().get("job"));
        PageObjectInitiator.getObjectRestApi().logResponseInReport(201, response);
        PageObjectInitiator.getObjectRestApi().setTestAttribute(response,context, "id");
    }

    @Test(description = "Verify Put Data for Sample API", dataProvider = "commonDataProvider", dataProviderClass = DataProvider.class)
    public void TC03_PutSomeData(Map<String, String> data, ITestContext context) {
        setTestData(data);

        Response response = PageObjectInitiator.getObjectRestApi().restApiPatchRequest(context);
        PageObjectInitiator.getObjectRestApi().verifyResponseCode(200, response);
        PageObjectInitiator.getObjectRestApi().validateResponseBodyContains(response,getTestData().get("name"));
        PageObjectInitiator.getObjectRestApi().validateResponseBodyContains(response,getTestData().get("job"));
        PageObjectInitiator.getObjectRestApi().logResponseInReport(200, response);
    }

}
