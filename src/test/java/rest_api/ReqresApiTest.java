package rest_api;

import jdk.jfr.Description;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.GDateTimeUtil;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.responseSpecification;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ReqresApiTest {

    @BeforeAll
    static void init() {
        Specifications.installSpecifications();
    }

    @Test
    @Description("Получает список пользователей на странице 2 и ищет среди них пользователя с фамилией Ferguson")
    @DisplayName(value = "Наличие пользователя с фамилией Ferguson")
    void getUsersList() {
        given()
                .get("/api/users?page=2")
                .then()
                .statusCode(200)
                .body("data[1].last_name", equalTo("Ferguson"));
    }

    @Test
    @Description("Создает нового пользователя и проверяет актуальность даты создания")
    @DisplayName("Создание нового пользователя")
    void createUser() {
        Map<String, String> data = new HashMap<>(Map.of(
                "name", "morpheus",
                "job", "leader"));
        long timestamp = GDateTimeUtil.timestampAsSeconds();
        String formattedCreatingTime = given()
                .body(data)
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract().path("createdAt");
        long creatingTime = GDateTimeUtil.isoFormatTimeToSeconds(formattedCreatingTime);
        assertThat(creatingTime, Matchers.greaterThanOrEqualTo(timestamp));
    }

    @Test
    @Description("Обновляет данные пользователя в базе и проверяет актуальность даты")
    @DisplayName("Обновление данных пользователя")
    void updateUserDataWithPutMethod() {
        Map<String, String> data = new HashMap<>(Map.of(
                "name", "morpheus",
                "job", "zion resident"));
        long timestamp = GDateTimeUtil.timestampAsSeconds();
        String formattedUpdateTime = given()
                .body(data)
                .when()
                .put("/api/users/2")
                .then()
                .statusCode(200)
                .extract().path("updatedAt");
        long updatingTime = GDateTimeUtil.isoFormatTimeToSeconds(formattedUpdateTime);
        assertThat(updatingTime, Matchers.greaterThanOrEqualTo(timestamp));
    }

    @Test
    @Description("Удаляет пользователя из базы")
    @DisplayName("Удаление пользователя из базы")
    void deleteUser() {
        responseSpecification.contentType("");
        given()
                .when()
                .delete("/api/users/2")
                .then()
                .statusCode(204);
    }

    @Test
    @Description("Выполняет регистрацию нового пользователя")
    @DisplayName("Регистрация нового пользователя")
    void registerUser() {
        Map<String, String> data = new HashMap<>(Map.of(
                "email", "eve.holt@reqres.in",
                "password", "pistol"));
        given()
                .body(data)
                .when()
                .post("/api/register")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .and()
                .body("token", not(empty()));
    }

    @Test
    @Description("Выполняет регистрацию нового пользователя")
    @DisplayName("Регистрация нового пользователя")
    void UnsuccessfulAuthorization() {
        Map<String, String> data = new HashMap<>(Map.of(
                "email", "peter@klaven"));
        given()
                .body(data)
                .when()
                .post("/api/login")
                .then()
                .statusCode(400)
                .body("error", containsString("Missing password"));
    }
}
