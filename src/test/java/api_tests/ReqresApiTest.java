package api_tests;

import jdk.jfr.Description;
import models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spec.Specifications;
import utils.GDateTimeUtil;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.responseSpecification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ReqresApiTest {

	@BeforeAll
	static void init() {
		Specifications.installSpecifications();
	}

	@Test
	@Description("Запрашивает список пользователей на странице 2 и проверяет, " +
			"что среди них есть пользователь с фамилией Ferguson")
	@DisplayName(value = "Наличие пользователя с фамилией Ferguson")
	void getUsers() {
		given()
				.get("/users?page=2")
				.then()
				.statusCode(200)
				.body("data[1].last_name", equalTo("Ferguson"));
	}

	@Test
	@Description("Проверяет возможность создания нового пользователя и актуальность даты создания")
	@DisplayName("Создание нового пользователя")
	void createUser() {
		long timestampAsSeconds = GDateTimeUtil.timestampInSeconds();

		CreateUserRequestPojoModel requestBody = CreateUserRequestPojoModel.builder()
				.name("morpheus")
				.job("leader").build();

		CreateUserResponsePojoModel responseBody = given()
				.body(requestBody)
				.when()
				.post("/users")
				.then()
				.statusCode(201)
				.extract().as(CreateUserResponsePojoModel.class);

		long creatingTime = GDateTimeUtil.isoFormatTimeToSeconds(responseBody.getCreatedAt());

		assertThat(responseBody.getName()).isEqualTo(requestBody.getName());
		assertThat(responseBody.getJob()).isEqualTo(requestBody.getJob());
		assertThat(responseBody.getId()).containsOnlyDigits();
		assertThat(creatingTime).isGreaterThanOrEqualTo(timestampAsSeconds);
	}


	@Test
	@Description("Проверяет возможность обновления данных пользователя в базе и актуальность даты обновления")
	@DisplayName("Обновление данных пользователя")
	void updateUserData() {
		long timestampAsSeconds = GDateTimeUtil.timestampInSeconds();

		CreateUserRequestPojoModel requestBody = CreateUserRequestPojoModel.builder()
				.name("morpheus")
				.job("zion resident").build();

		UpdateUserResponsePojoModel responseBody = given()
				.body(requestBody)
				.when()
				.put("/users/2")
				.then()
				.statusCode(200)
				.extract().as(UpdateUserResponsePojoModel.class);

		long updatingTime = GDateTimeUtil.isoFormatTimeToSeconds(responseBody.getUpdatedAt());
		assertThat(responseBody.getName()).isEqualTo(requestBody.getName());
		assertThat(responseBody.getJob()).isEqualTo(requestBody.getJob());
		assertThat(updatingTime).isGreaterThanOrEqualTo(timestampAsSeconds);
	}

	@Test
	@Description("Удаляет пользователя из базы")
	@DisplayName("Удаление пользователя из базы")
	void deleteUser() {
		responseSpecification.contentType("");
		given()
				.when()
				.delete("/users/2")
				.then()
				.statusCode(204);
	}

	@Test
	@Description("Проверяется возможность регистрации нового пользователя")
	@DisplayName("Регистрация нового пользователя")
	void registerUser() {
		RegisterUserRequestPojoModel requestBody = RegisterUserRequestPojoModel.builder()
				.email("eve.holt@reqres.in")
				.password("pistol").build();

		RegisterUserResponsePojoModel responseBody = given()
				.body(requestBody)
				.when()
				.post("/register")
				.then()
				.statusCode(200)
				.extract().as(RegisterUserResponsePojoModel.class);

		assertThat(responseBody.getId()).isInstanceOf(Integer.class);
		assertThat(responseBody.getToken()).isNotNull();
	}

	@Test
	@Description("Проверяется невозможность авторизации пользователя без пароля")
	@DisplayName("Неуспешная авторизации пользователя без пароля")
	void UnsuccessfulAuthorization() {
		UnsuccessfulAuthorizationRequestPojoModel requestBody = UnsuccessfulAuthorizationRequestPojoModel.builder()
				.email("peter@klaven").build();

		UnsuccessfulAuthorizationResponsePojoModel responseBody = given()
				.body(requestBody)
				.when()
				.post("/login")
				.then()
				.statusCode(400)
				.extract().as(UnsuccessfulAuthorizationResponsePojoModel.class);

		assertThat(responseBody.getError()).isEqualTo("Missing password");
	}
}
