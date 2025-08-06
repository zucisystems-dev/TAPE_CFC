package framework.api;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import static framework.api.RestApi.returnJSONObject;
import static framework.utils.WebActions.logMessageInReport;

/**
 * Request filter for RestAssured to log request and response details.
 * Implements the RestAssured Filter interface.
 */
public class RestAssuredRequestFilter implements Filter {

	/**
	 * Filters the RestAssured request and response, logging relevant details.
	 *
	 * @param requestSpec  The request specification.
	 * @param responseSpec The response specification.
	 * @param ctx          The filter context.
	 * @return The filtered response.
	 */
	@Override
	public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec,
	        FilterContext ctx) {
	    Response response = ctx.next(requestSpec, responseSpec);

	    /*int maxLines =20;
	    String logMessage = requestSpec.getMethod() + " \n" + requestSpec.getURI() + " \n Request Body =>"
	            + requestSpec.getHeaders() + "\n" + requestSpec.getBody() + " \n-----------------\n Response Status => "
	            + response.getStatusCode() + "\n" + response.getStatusLine() + " \n Response Body => "
	            + response.getBody().asPrettyString();

	    // Split the log message into lines
	    String[] lines = logMessage.split("\r\n|\r|\n");

	    // Log only the first 20 lines
	    StringBuilder truncatedLog = new StringBuilder();
	    for (int i = 0; i < Math.min(lines.length, maxLines); i++) {
	        truncatedLog.append(lines[i]).append("\n");
	    }*/
		logMessageInReport("Request URL : <br>" + requestSpec.getURI());

		String requestHeaders = requestSpec.getHeaders().toString();
		logMessageInReport("Request Headers : <br>" + requestHeaders);

		String requestJSON = requestSpec.getBody().toString();
		logMessageInReport("Request Body : <br>" + returnJSONObject(requestJSON));

	    return response;
	}

}
