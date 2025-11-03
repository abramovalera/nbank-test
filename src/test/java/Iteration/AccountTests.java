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
public class AccountTests {

    @Test
    public void userVovaCanCreateAccount() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic dm92YTpUZXN0MTIzNCR=")
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void userAnnaCanCreateAccount() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YW5uYTpUZXN0MTIzNCR=")
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void userVovaCanDeposit500() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic dm92YTpUZXN0MTIzNCR=")
                .body("""
                        {
                          "id": 1,
                          "balance": 500
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void userAnnaCanDeposit300() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YW5uYTpUZXN0MTIzNCR=")
                .body("""
                        {
                          "id": 2,
                          "balance": 300
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    public static Stream<Arguments> validDepositAmounts() {
        return Stream.of(
                Arguments.of(1, 5000.0),
                Arguments.of(1, 0.01)
        );
    }

    @MethodSource("validDepositAmounts")
    @ParameterizedTest
    public void userCanDepositBoundaryValues(int accountId, double amount) {
        String requestBody = String.format(
                """
                {
                  "id": %d,
                  "balance": %.2f
                }
                """, accountId, amount);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic dm92YTpUZXN0MTIzNCR=")
                .body(requestBody)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    public static Stream<Arguments> invalidDepositData() {
        return Stream.of(
                Arguments.of(1, -50.0),
                Arguments.of(1, 5001.0),
                Arguments.of(1, 5000.01)
        );
    }

    @MethodSource("invalidDepositData")
    @ParameterizedTest
    public void userCannotDepositInvalidAmounts(int accountId, double amount) {
        String requestBody = String.format(
                """
                {
                  "id": %d,
                  "balance": %.2f
                }
                """, accountId, amount);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic dm92YTpUZXN0MTIzNCR=")
                .body(requestBody)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void userCannotDepositToForeignAccount() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic dm92YTpUZXN0MTIzNCR=")
                .body("""
                        {
                          "id": 999,
                          "balance": 100
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void cannotDepositWithoutAuthorization() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "id": 1,
                          "balance": 100
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void userVovaCanGetAccountBalance() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic dm92YTpUZXN0MTIzNCR=")
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void userAnnaCanGetAccountBalance() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YW5uYTpUZXN0MTIzNCR=")
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void userVovaCanGetAccountTransactions() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic dm92YTpUZXN0MTIzNCR=")
                .get("http://localhost:4111/api/v1/accounts/1/transactions")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void userAnnaCanGetAccountTransactions() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YW5uYTpUZXN0MTIzNCR=")
                .get("http://localhost:4111/api/v1/accounts/2/transactions")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }
}