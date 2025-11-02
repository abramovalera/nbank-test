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
public class TransferTests {

    @Test
    public void userVovaCanTransferToAnna() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic dm92YTpUZXN0MTIzNCR=")
                .body("""
                        {
                          "senderAccountId": 1,
                          "receiverAccountId": 2,
                          "amount": 100
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    public static Stream<Arguments> validTransferAmounts() {
        return Stream.of(
                Arguments.of(1, 2, 10000.0),
                Arguments.of(1, 2, 0.01)
        );
    }

    @MethodSource("validTransferAmounts")
    @ParameterizedTest
    public void userCanTransferBoundaryValues(int senderAccountId, int receiverAccountId, double amount) {
        String requestBody = String.format(
                """
                {
                  "senderAccountId": %d,
                  "receiverAccountId": %d,
                  "amount": %.2f
                }
                """, senderAccountId, receiverAccountId, amount);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic dm92YTpUZXN0MTIzNCR=")
                .body(requestBody)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    public static Stream<Arguments> invalidTransferData() {
        return Stream.of(
                Arguments.of(1, 2, 1000.0),
                Arguments.of(1, 2, -10.0),
                Arguments.of(1, 2, 10001.0),
                Arguments.of(1, 2, 10000.01)
        );
    }

    @MethodSource("invalidTransferData")
    @ParameterizedTest
    public void userCannotTransferInvalidAmounts(int senderAccountId, int receiverAccountId, double amount) {
        String requestBody = String.format(
                """
                {
                  "senderAccountId": %d,
                  "receiverAccountId": %d,
                  "amount": %.2f
                }
                """, senderAccountId, receiverAccountId, amount);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic dm92YTpUZXN0MTIzNCR=")
                .body(requestBody)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void userCannotTransferFromForeignAccount() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YW5uYTpUZXN0MTIzNCR=")
                .body("""
                        {
                          "senderAccountId": 1,
                          "receiverAccountId": 2,
                          "amount": 100
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void cannotTransferWithoutAuthorization() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "senderAccountId": 1,
                          "receiverAccountId": 2,
                          "amount": 100
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }
}