package framework.api;

import static framework.utils.PayloadBuilder.buildJsonPayload;
import static framework.utils.WebActions.logMessageFailInReport;
import static framework.utils.WebActions.logMessageInReport;
import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.*;
import framework.config.PropertyReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Assert;

import com.jayway.jsonpath.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.ITestContext;

public class RestApi {
	protected Response apiResponse;
	protected RequestSpecification requestSpecification;
	protected  Integer responseStatus;
	String restApiCase = "RestApi";
	String contentTypeHeader = "Content-type";
	String passBodyAssertion = "Body Validation Passed";
	String failBodyAssertion = "Body Validation Failed";
	String currentWorkingDirectory = "user.dir";
	String contentTypeJson = "application/json";
	String apiTokenHeader = "x-api-token";
	String apiKeyHeader = "x-api-key";
	String apiKeyValue = "reqres-free-v1";
	String bearerString = "Bearer ";
	String authorizationHeader = "Authorization";
	String restPostText = "RestPost";
	String ihAdminText = "IHAdmin";
	String ihAdminAccessToken = "IhAdminAccessToken";
	String signInText = "sign-in";
	String validTokensText = "validtokens";
	String patientText = "Patient";
	String jsonExtension = ".json";
	String accessTokenJsonPath = "$.accessToken";
	static String url = "";

	public String nistGenerator() {
		//		Random random = new Random((new Date()).getTime());   as per java:S2245
		SecureRandom random = new SecureRandom();
		random.setSeed((new Date()).getTime());
		char[] values = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
				't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

		String out = "";

		for (int i = 0; i < 15; i++) {
			int idx = random.nextInt(values.length);
			out += values[idx];
		}

		return out;

	}

	public String mrGenerator() {
		try {
			SecureRandom secureRandom = new SecureRandom();
			int secureRandomNumber = secureRandom.nextInt(999999);
			return "000000000"+secureRandomNumber;
		} catch (Exception e) {
			return null;
		}
	}

	public void validateResponseCode(Response response,String user,String sheetName,String codeVariable) {
		try {
			int code = response.getStatusCode();
			String statusCode = String.valueOf(code);
			String postStatusCode = "200";
			if (statusCode.contains(postStatusCode)) {
				Assert.assertTrue(true, "Response Code Validation Passed");
			} else {
				Assert.assertTrue(false, "Response Code Validation Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void validateResponseBody(Response response,String user,String sheetName,String bodyVariable) {
		try {
			String responseBody = response.getBody().asString();
			String inputData = "Request";
			if (bodyVariable.contains("Expected Task Titles")) {
				String[] split = inputData.split("\\|");
				for (int i = 0; i < split.length; i++) {
					if (responseBody.contains(split[i])) {
						Assert.assertTrue(true, passBodyAssertion);
					} else {
						Assert.assertTrue(false, failBodyAssertion);
					}
				}
			}	else {
				if (responseBody.contains(inputData)) {
					Assert.assertTrue(true, passBodyAssertion);
				} else {
					Assert.assertTrue(false, failBodyAssertion);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getJsonObjectSpecificValue(Response response,String jsonObject,String jsonArray,String data) {
		String object =null;
		String replace= null;
		try {
			String responseString = response.getBody().asString();
			JsonObject jsonResponse = (JsonObject) new JsonParser().parse(responseString);
			object =jsonResponse.getAsJsonObject(jsonObject).getAsJsonArray(jsonArray).get(0).getAsJsonObject().get(data).toString();
			replace = object.replace("\"", "");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return replace;
	}

	public void validateResponseBodyDirect(Response response,String bodyVariable) {
		try {

			String responseBody = response.getBody().asString();
			if (responseBody.contains(bodyVariable)) {
				Assert.assertTrue(true, passBodyAssertion);
			} else {
				Assert.assertTrue(false, failBodyAssertion);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getJSONObjectData(String fileName,String caseType,String caseVar) {
		JSONParser parser = new JSONParser();
		String dataValue = null;
		String filePathIn = "";
		filePathIn = System.getProperty(currentWorkingDirectory) + "\\src\\data\\" + "inputDataProd" + jsonExtension;

		try {
			JSONObject jsonobj = (JSONObject) parser.parse(new FileReader(filePathIn));
			JSONObject subJsonobj = (JSONObject) jsonobj.get(caseType);
			dataValue = (String) subJsonobj.get(caseVar);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataValue;
	}
	public String getTwoJsonObjectData(Response response,String firstObj,String secondObj,String data) {
		String object =null;
		String replace= null;
		try {
			String responseString = response.getBody().asString();
			JsonObject jsonResponse = (JsonObject) new JsonParser().parse(responseString);
			object=jsonResponse.getAsJsonObject(firstObj).getAsJsonObject(secondObj).get(data).toString();
			replace = object.replace("\"", "");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return replace;
	}
	public String getJSONTwoObjectData(String fileName,String caseType,String caseType1,String caseVar) {
		JSONParser parser = new JSONParser();
		String dataValue = null;
		String filePathIn = "";
		filePathIn = System.getProperty(currentWorkingDirectory) + "\\src\\data\\" + "inputDataProd" + jsonExtension;
		try {
			JSONObject jsonobj = (JSONObject) parser.parse(new FileReader(filePathIn));
			JSONObject subJsonobj = (JSONObject) jsonobj.get(caseType);
			JSONObject subJsonobj1 = (JSONObject) subJsonobj.get(caseType1);
			dataValue = (String) subJsonobj1.get(caseVar);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataValue;
	}

	public String getJsonObjectSpecificArray(Response response,String jsonArray,String data) {
		String object =null;
		String replace= null;
		try {
			String responseString = response.getBody().asString();
			JsonObject jsonResponse = (JsonObject) new JsonParser().parse(responseString);
			object = jsonResponse.getAsJsonObject().getAsJsonArray(jsonArray).get(0).getAsJsonObject().get(data).toString();
			replace = object.replace("\"", "");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return replace;
	}
	public String getJsonDynamicObjectSpecificArray(Response response,String jsonArray,String firObj,String data) {
		String object =null;
		String replace= null;
		try {
			String responseString = response.getBody().asString();
			JsonObject jsonResponse = (JsonObject) new JsonParser().parse(responseString);
			object = jsonResponse.getAsJsonObject().getAsJsonArray(jsonArray).get(0).getAsJsonObject().getAsJsonObject(firObj).get(data).toString();
			replace = object.replace("\"", "");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return replace;
	}

	public String getCurrentClassNameToFindHTTP() {
		String hTTPReturn=null;
		try {
			String className = this.getClass().getSimpleName();
			String[] split = className.split("\\_");   
			hTTPReturn = split[1];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hTTPReturn;
	}

	public JsonObject updateJSONArray(JsonObject asJsonObjectDemo,String caseVariable, String key ,long caseVar,String sheetName) {
		try {
			JsonElement asJsonElement = asJsonObjectDemo.get(caseVariable);
			JsonObject asJsonObject = null;
			switch (key) {
			case "diastolic":
				asJsonObject =  (JsonObject) asJsonElement.getAsJsonArray().get(1);
				break;
			case "pulserate":
				asJsonObject =  (JsonObject) asJsonElement.getAsJsonArray().get(2);
				break;
			default:
				asJsonObject =  (JsonObject) asJsonElement.getAsJsonArray().get(0);
				break;
			}
			asJsonObject.addProperty("ts", caseVar);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return asJsonObjectDemo;
	}

	public List<String> getJsonArrayValueSpecificToAppointments(Response response,String data) {
		JsonElement jsonElement =null;
		String singleAppointmentId= null;
		List<String> allAppointmentId = new ArrayList<String>();

		try {
			String responseString = response.getBody().asString();

			JsonArray jsonResponse = (JsonArray) new JsonParser().parse(responseString);
			for (int i = 0; i < jsonResponse.size(); i++) {
				jsonElement = jsonResponse.get(i).getAsJsonObject().get(data);
				singleAppointmentId=jsonElement.toString();
				allAppointmentId.add(singleAppointmentId);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return allAppointmentId;
	}
	public String getJsoNSpecificLastArray(Response response, String element) {
		String blockId =null; 
		try {
			String responseString = response.getBody().asString();
			JsonArray jsonResponse = (JsonArray) new JsonParser().parse(responseString);
			int size = jsonResponse.size();
			JsonElement jsonElement = jsonResponse.get(size- 1).getAsJsonObject().get(element);
			blockId=jsonElement.toString();


		}catch (Exception e) {
			e.printStackTrace();
		}
		return blockId;


	}
	//My methods
	public JsonElement jsonTestUpdateValue(JsonElement jsonElement,String data,String value ) {

		int numValue;
		JsonArray jsonarray = new JsonArray();
		JsonArray jsonelementArray = null;
		JsonObject jsonElementObject = null;
		//To perform operations in a JsonArray
		if(jsonElement.isJsonArray()) { 
			jsonelementArray = jsonElement.getAsJsonArray();
			jsonelementArray.get(0).getAsJsonObject().remove(data);
			if(value.matches("\\d+")){
				numValue = Integer.parseInt(value);
				jsonelementArray.get(0).getAsJsonObject().addProperty(data, numValue);
			}
			else {
				jsonelementArray.get(0).getAsJsonObject().addProperty(data, value);
			}
			return jsonelementArray;
		}
		else if(jsonElement.isJsonObject()){
			jsonElementObject = jsonElement.getAsJsonObject();
			boolean isArray = jsonElementObject.get(data).isJsonArray();
			jsonElementObject.remove(data);
			//To perform operations in JSON containing JsonArray
			if(value.matches("\\d+")&& isArray) {
				numValue = Integer.parseInt(value);
				jsonarray.add(numValue);
				jsonElementObject.getAsJsonObject().add(data, jsonarray);
			}
			//To perform operations in JSON
			else if(value.matches("\\d+")){
				numValue = Integer.parseInt(value);
				jsonElementObject.getAsJsonObject().addProperty(data, numValue);
			}
			else {
				jsonElementObject.getAsJsonObject().addProperty(data, value);
			}
			return jsonElementObject;
		}
		return null;


	}

	//Get data From a Json
	public String getJsonDetails(Response response,String jsonpath) {
		String object =null;
		String replace= null;
		try {
			String responseString = response.getBody().asString();
			object = JsonPath.read(responseString,jsonpath).toString();
			replace = object.replace("\"", "");  			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return replace;
	}

	public JsonObject updateInputDataJsonTest(JsonObject inputDataJson,String paramVariable,String caseVariable)
	{
		int intValue;
		try{
			if (caseVariable.matches("\\d+")) {
				intValue = Integer.parseInt(caseVariable);
				inputDataJson.addProperty(paramVariable, intValue);
			}
			else {
				inputDataJson.addProperty(paramVariable, caseVariable);
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		return inputDataJson;
	}
	//Get Request Headers
	public RequestSpecification getHeaders(String apiToken,String accessToken,String type) {
		requestSpecification = given()
				.header(contentTypeHeader, contentTypeJson);
		if(type.equalsIgnoreCase("both")||type.equalsIgnoreCase(validTokensText)) {
			requestSpecification = requestSpecification.header(apiTokenHeader,apiToken)
					.header(authorizationHeader, bearerString+accessToken);
		}
		else if(type.equalsIgnoreCase("accessToken")) {
			requestSpecification = requestSpecification.header(authorizationHeader, bearerString+accessToken);
		}
		else if(type.equalsIgnoreCase("apiToken")|| type.equalsIgnoreCase("general")) {
			requestSpecification = requestSpecification.header(apiTokenHeader,apiToken);
		}
		return requestSpecification;
	}

	public RequestSpecification getHeaders(String apiToken,String accessToken,String type,String parameter) {
		String filePath= System.getProperty(currentWorkingDirectory)+"\\src\\data\\SampleUpload.pdf";
		File file=new File(filePath);
		requestSpecification = given()
				.header(contentTypeHeader, contentTypeJson)
				.urlEncodingEnabled(false)
				.multiPart("file", new File(file.toURI()), "application/pdf")
				.formParam("id", parameter);

		if(type.equalsIgnoreCase("both")||type.equalsIgnoreCase(validTokensText)) {
			requestSpecification = requestSpecification.header(apiTokenHeader,apiToken)
					.header(authorizationHeader, bearerString+accessToken);
		}
		else if(type.equalsIgnoreCase("accessToken")) {
			requestSpecification = requestSpecification	.header(authorizationHeader, bearerString+accessToken);
		}
		else if(type.equalsIgnoreCase("apiToken")|| type.equalsIgnoreCase("general")) {
			requestSpecification = requestSpecification.header(apiTokenHeader,apiToken);
		}
		return requestSpecification;
	}
	public Response restApiPostRequest(ITestContext context) {
		try {
			String[] paramArray = context.getCurrentXmlTest().getParameter("env").split("_");
			url = PropertyReader.getProperty(paramArray[1],"postAPIURL");
			apiResponse = given()
					.filter(new RestAssuredRequestFilter())
					.header(contentTypeHeader, contentTypeJson)
					.header(apiKeyHeader,apiKeyValue)
					.and()
					.body(buildJsonPayload().toString())
					.when()
					.post(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiResponse;
	}

	public Response restApiPutRequest(String apiUrl) {
		try{
			System.out.println("API URL: " + apiUrl);
			apiResponse = given()
					.filter(new RestAssuredRequestFilter())
					.header(contentTypeHeader, contentTypeJson)
					.header(apiKeyHeader,apiKeyValue)
					.body(buildJsonPayload().toString())
					.when()
					.put(apiUrl);

			responseStatus = apiResponse.getStatusCode();

		}catch (Exception e){
			e.printStackTrace();
		}
		return apiResponse;
	}

	public Response restApiPatchRequest(ITestContext context) {
		try{
			String[] paramArray = context.getCurrentXmlTest().getParameter("env").split("_");
			url = PropertyReader.getProperty(paramArray[1],"postAPIURL");
			url = url + "/" + context.getAttribute("testAttribute");
			apiResponse = given()
					.filter(new RestAssuredRequestFilter())
					.header(contentTypeHeader, contentTypeJson)
					.header(apiKeyHeader,apiKeyValue)
					.body(buildJsonPayload().toString())
					.patch(url);
		}catch (Exception e){
			e.printStackTrace();
		}
		return apiResponse;
	}

	public Response restApiGetRequest(ITestContext context) {
		try {
			String[] paramArray = context.getCurrentXmlTest().getParameter("env").split("_");
			url = PropertyReader.getProperty(paramArray[1],"getAPIURL");
			apiResponse = given()
					.header(contentTypeHeader, contentTypeJson)
					.header(apiKeyHeader,apiKeyValue)
					.get(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiResponse;
	}
	public Response restApiDeleteRequest(String apiToken,String accessToken,String apiUrl,int statusCode,String type) {
		String stagingUrl = "URL";
		try {
			apiResponse = getHeaders(apiToken, accessToken, type)
					.and()
					.when()
					.delete(stagingUrl+apiUrl)
					.then()
					.statusCode(statusCode)
					.extract().response();

			responseStatus = apiResponse.getStatusCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiResponse;
	}
	
	public void validateResponseBodyNotContains(Response response) {
		try {
			String responseBody = response.getBody().asString();
			String inputData = "SUCCESS";
			if (!responseBody.contains(inputData)) {
				Assert.assertTrue(true, passBodyAssertion);
			} else {
				Assert.assertTrue(false, failBodyAssertion);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void verifyResponseCode(int expectedStatusCode, Response response){
		if(response.getStatusCode() == expectedStatusCode){
			logMessageInReport("Response contains expected status Code : " + response.getStatusCode());
		}else{
			logMessageFailInReport("Response does not contains expected status code : " + response.getStatusCode());
		}
	}

	public void setTestAttribute(Response response, ITestContext context, String attribute){
		String testAttribute = response.jsonPath().getString(attribute);
		context.setAttribute("testAttribute", testAttribute);
	}

	public void logResponseInReport(int expectedStatusCode, Response response){
		String rawJson = response.getBody().asString();
		if(response.getStatusCode() == expectedStatusCode){
			logMessageInReport("Received Expected Response : <br>" + returnJSONObject(rawJson));
			logMessageInReport("Response time is " + response.timeIn(TimeUnit.SECONDS));
		}else{
			logMessageFailInReport("Didn't receive expected Response : <br>" + returnJSONObject(rawJson));
			logMessageFailInReport("Response time is " + response.timeIn(TimeUnit.SECONDS));
		}
	}

	public static String returnJSONObject(String jsonObject){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = "<pre>" + gson.toJson(JsonParser.parseString(jsonObject)) + "</pre>";
		return prettyJson;
	}

	public void validateResponseBodyContains(Response response, String expectedValue){
		String responseBody = response.getBody() != null ? response.getBody().asString() : "";

		if (responseBody.contains(expectedValue)) {
			logMessageInReport("Response contains expected value: " + expectedValue);
		} else {
			logMessageFailInReport("Response does NOT contain expected value: " + expectedValue);
			Assert.fail("Response validation failed. Expected value not found: " + expectedValue);
		}
	}
}