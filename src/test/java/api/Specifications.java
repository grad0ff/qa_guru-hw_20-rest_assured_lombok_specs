package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.*;

public class Specifications {

	private static final String baseUri = "https://reqres.in";
	private static final String basePath = "/api";

	private static RequestSpecification setRequestSpec() {
		return with()
				.baseUri(baseUri)
				.basePath(basePath)
				.contentType(ContentType.JSON);
	}

	private static ResponseSpecification setResponseSpec() {
		return with()
				.then()
				.contentType(ContentType.JSON);
	}

	static void installSpecifications() {
		requestSpecification = setRequestSpec();
		responseSpecification = setResponseSpec();
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	}
}
