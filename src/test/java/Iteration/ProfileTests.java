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
public class ProfileTests {

    @Test
    public void userVovaCanUpdateProfileName() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic dm92YTpUZXN0MTIzNCR=")
                .body("""
                        {
                          "name": "Vova Ivanov"
                        }
                        """)
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    public static Stream<Arguments> invalidNameData() {
        return Stream.of(
                Arguments.of("Vova"),
                Arguments.of("Vova 123"),
                Arguments.of("Vova Ivanov Petrov"),
                Arguments.of("Vova@ Ivanov")
        );
    }

    @MethodSource("invalidNameData")
    @ParameterizedTest
    public void userCannotUpdateProfileWithInvalidName(String name) {
        String requestBody = String.format(
                """
                {
                  "name": "%s"
                }
                """, name);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic dm92YTpUZXN0MTIzNCR=")
                .body(requestBody)
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void userVovaCanGetProfile() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic dm92YTpUZXN0MTIzNCR=")
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }
}