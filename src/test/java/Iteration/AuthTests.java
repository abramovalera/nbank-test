package Iteration;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

@ExtendWith(RestAssuredSetupExtension.class)
public class AuthTests {

    @Test
    public void adminCanGenerateAuthToken() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "username": "admin",
                          "password": "admin"
                        }
                        """)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void adminCanCreateUserVova() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                        {
                          "username": "vova",
                          "password": "Test1234$",
                          "role": "USER"
                        }
                        """)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void adminCanCreateUserAnna() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                        {
                          "username": "anna",
                          "password": "Test1234$",
                          "role": "USER"
                        }
                        """)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void userVovaCanLogin() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "username": "vova",
                          "password": "Test1234$"
                        }
                        """)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void userAnnaCanLogin() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "username": "anna",
                          "password": "Test1234$"
                        }
                        """)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    public static Stream<Arguments> validUsernameData() {
        return Stream.of(
                Arguments.of("abc", "Test1234$", "USER"),
                Arguments.of("abcdefghijklmno", "Test1234$", "USER"),
                Arguments.of("USERUSER", "Test1234$", "USER"),
                Arguments.of("useruserr", "Test1234$", "USER"),
                Arguments.of("user.name", "Test1234$", "USER"),
                Arguments.of("user-name", "Test1234$", "USER"),
                Arguments.of("user_name", "Test1234$", "USER"),
                Arguments.of("user123", "Test1234$", "USER")
        );
    }

    @MethodSource("validUsernameData")
    @ParameterizedTest
    public void adminCanCreateUserWithValidUsernameVariations(String username, String password, String role) {
        String requestBody = String.format(
                """
                        {
                          "username": "%s",
                          "password": "%s",
                          "role": "%s"
                        }
                        """, username, password, role);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body(requestBody)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED);
    }

    public static Stream<Arguments> invalidUsernameData() {
        return Stream.of(
                Arguments.of("bc", "Test1234$", "USER"),
                Arguments.of("testttestttesttt", "Test1234$", "USER"),
                Arguments.of("", "Test1234$", "USER"),
                Arguments.of("name user", "Test1234$", "USER"),
                Arguments.of("bca$", "Test1234$", "USER")
        );
    }

    @MethodSource("invalidUsernameData")
    @ParameterizedTest
    public void adminCannotCreateUserWithInvalidUsername(String username, String password, String role) {
        String requestBody = String.format(
                """
                        {
                          "username": "%s",
                          "password": "%s",
                          "role": "%s"
                        }
                        """, username, password, role);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body(requestBody)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    public static Stream<Arguments> invalidPasswordData() {
        return Stream.of(
                Arguments.of("useremptypass", "", "USER"),
                Arguments.of("usernodigit", "Password$", "USER"),
                Arguments.of("usernoupp", "password1$", "USER"),
                Arguments.of("usernolow", "PASSWORD1$", "USER"),
                Arguments.of("usernocpez", "Password1", "USER"),
                Arguments.of("probeluser", "Pass word1$", "USER"),
                Arguments.of("shortpass", "Test1$", "USER")
        );
    }

    @MethodSource("invalidPasswordData")
    @ParameterizedTest
    public void adminCannotCreateUserWithInvalidPassword(String username, String password, String role) {
        String requestBody = String.format(
                """
                        {
                          "username": "%s",
                          "password": "%s",
                          "role": "%s"
                        }
                        """, username, password, role);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body(requestBody)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void adminCannotCreateUserWithInvalidRole() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                        {
                          "username": "userpuzer",
                          "password": "Test1234$",
                          "role": "QA"
                        }
                        """)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void adminCannotCreateUserWithExistingUsername() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                        {
                          "username": "vova",
                          "password": "Test1234$",
                          "role": "USER"
                        }
                        """)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void cannotCreateUserWithoutAuthorization() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "username": "hacker",
                          "password": "Test1234$",
                          "role": "USER"
                        }
                        """)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void userCannotAccessAdminEndpoint() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic dm92YTpUZXN0MTIzNCR=")
                .body("""
                        {
                          "username": "testuser",
                          "password": "Test1234$",
                          "role": "USER"
                        }
                        """)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void adminCanGetAllUsers() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .get("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }
}