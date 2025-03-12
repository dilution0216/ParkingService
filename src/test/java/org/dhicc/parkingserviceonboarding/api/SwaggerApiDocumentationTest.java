package org.dhicc.parkingserviceonboarding.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SwaggerApiDocumentationTest {

    @LocalServerPort
    private int port;

    /** ✅ 1. Swagger UI가 정상적으로 열리는지 확인 */
    @Test
    void testSwaggerUiLoads() {
        RestAssured.given()
                .baseUri("http://localhost:" + port)
                .when().get("/swagger-ui/index.html")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    /** ✅ 2. OpenAPI JSON 문서가 정상적으로 반환되는지 확인 */
    @Test
    void testOpenApiJsonExists() {
        RestAssured.given()
                .baseUri("http://localhost:" + port)
                .when().get("/v3/api-docs")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("openapi", equalTo("3.0.1"))  // ✅ OpenAPI 버전 확인
                .body("paths", not(empty())); // ✅ 문서에 정의된 API가 있는지 확인
    }

    /** ✅ 3. 특정 엔드포인트가 Swagger 문서에 존재하는지 검증 */
    @Test
    void testSubscriptionApiExistsInDocs() {
        Response response = RestAssured.given()
                .baseUri("http://localhost:" + port)
                .when().get("/v3/api-docs")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        // ✅ Subscription API가 문서에 정의되어 있는지 확인
        String responseBody = response.getBody().asString();
        assert responseBody.contains("/subscription/register");
        assert responseBody.contains("/subscription/admin/{vehicleNumber}");
    }
}
