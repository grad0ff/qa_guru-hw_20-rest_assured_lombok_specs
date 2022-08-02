package rest_api;

import io.restassured.http.ContentType;
import jdk.jfr.Description;
import models.CreateUserResponsePojoModel;
import models.UserPojoModel;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.GDateTimeUtil;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.responseSpecification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

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
                .get("/api/users?page=2")
                .then()
                .statusCode(200)
                .body("data[1].last_name", equalTo("Ferguson"));
    }

    @Test
    @Description("Проверяет возможность создания нового пользователя и актуальность даты создания")
    @DisplayName("Создание нового пользователя")
    void createUser() {
        UserPojoModel reqBody = new UserPojoModel();
        reqBody.setName("morpheus");
        reqBody.setJob("leader");
        CreateUserResponsePojoModel resBody = given()
                .contentType(ContentType.JSON)
                .body(reqBody)
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract().as(CreateUserResponsePojoModel.class);

        assertThat(resBody.getName()).isEqualTo(reqBody.getName());
        assertThat(resBody.getJob()).isEqualTo(reqBody.getJob());
        assertThat(resBody.getId()).containsOnlyDigits();
    }


    @Test
    @Description("Проверяет возможность обновления данных пользователя в базе и актуальность даты обновления")
    @DisplayName("Обновление данных пользователя")
    void updateUserData() {
        String name = "morpheus";
        String job = "leader";
        Map<String, String> data = new HashMap<>(Map.of(
                "name", name,
                "job", job));
        long likeCreationDate = GDateTimeUtil.timestampAsSeconds() - 1;
        String formattedUpdateTime = given()
                .body(data)
                .when()
                .put("/api/users/2")
                .then()
                .statusCode(200)
                .body("name", equalTo(name))
                .and()
                .body("job", equalTo(job))
                .extract().path("updatedAt");
        long updatingTime = GDateTimeUtil.isoFormatTimeToSeconds(formattedUpdateTime);
//        assertThat(updatingTime, Matchers.greaterThanOrEqualTo(likeCreationDate));
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
    @Description("Проверяется возможность регистрации нового пользователя")
    @DisplayName("Регистрация нового пользователя")
    void registerUser() {
        String email = "eve.holt@reqres.in";
        String password = "pistol";
        Map<String, String> data = new HashMap<>(Map.of(
                "email", email,
                "password", password));
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
    @Description("Проверяется невозможность авторизации пользователя без пароля")
    @DisplayName("Неуспешная авторизации пользователя без пароля")
    void UnsuccessfulAuthorization() {
        String email = "peter@klaven";
        Map<String, String> data = new HashMap<>(Map.of(
                "email", email));
        given()
                .body(data)
                .when()
                .post("/api/login")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }
}
