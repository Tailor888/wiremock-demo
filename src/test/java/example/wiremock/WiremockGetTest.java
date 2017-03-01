package example.wiremock;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WiremockGetTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        baseURI = "http://localhost:8089";
        String url = "/test/.*";

        givenThat(get(urlMatching(url)).atPriority(9)
                .willReturn(aResponse()
                        .withStatus(400)));
    }

    @Test
    public void getResponseWithSuccess() {
        String url = "/test/resource";

        givenThat(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.TEXT_PLAIN.toString()).withBody("Success")));

        given().when().get(url).then().assertThat().statusCode(200);
    }

    @Test
    public void getResponseWithSuccessWithBody() {
        String url = "/test/resource/1";
        String sampleResponse = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Someone\",\n" +
                "  \"city\": \"Somewhere\"\n" +
                "}";

        givenThat(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(sampleResponse)));

        given().when().get(url).then().assertThat().statusCode(200).and().assertThat().body(Matchers.equalTo(sampleResponse));
    }

    @Test
    public void getResponseWithSuccessWithBodyForName() {
        String url = "/test/resource/1";
        String name = "Someone";
        String sampleResponse = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Someone\",\n" +
                "  \"city\": \"Somewhere\"\n" +
                "}";

        givenThat(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(sampleResponse)));

        given().when().get(url).then().assertThat().statusCode(200).and().assertThat().body("name", is(name));
    }

    @Test
    public void getResponseWithSuccessWithBodyForId() {
        String url = "/test/resource/1";
        Integer id = 5;
        String sampleResponse = "{\n" +
                "  \"id\": 5,\n" +
                "  \"name\": \"Someone\",\n" +
                "  \"city\": \"Somewhere\"\n" +
                "}";

        givenThat(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(sampleResponse)));

        given().when().get(url).then().assertThat().statusCode(200).and().assertThat().body("id", greaterThan(id - 1));
    }

    @Test
    public void getResponseForHigherPriorityWithStatus400() {
        String url = "/test/invalid";

        givenThat(get(urlEqualTo(url)).atPriority(10)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.TEXT_PLAIN.toString()).withBody("Success")));

        given().when().get(url).then().assertThat().statusCode(400);
    }
}
