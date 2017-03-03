package example.wiremock;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.text.MessageFormat;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

/**
 * Created by pkrawiec on 3/1/2017.
 */
public class WiremockMultipleTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Rule
    public WireMockRule wireMockRule2 = new WireMockRule(8090);

    @Before
    public void setUp() throws Exception {
        baseURI = "http://localhost";
        String url = "/test/.*";

        givenThat(get(urlMatching(url)).atPriority(9)
                .willReturn(aResponse()
                        .withStatus(400)));
    }

    @Test
    public void getResponsesFromTwoServices() {
        String url = "/test/resource";
        String url2 = "/test2/resource";

        wireMockRule.givenThat(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.TEXT_PLAIN.toString()).withBody("Hello")));

        wireMockRule2.givenThat(get(urlEqualTo(url2))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.TEXT_PLAIN.toString()).withBody("World")));

        String firstResponse = given().port(8089).when().get(url).then().assertThat().statusCode(200).extract().asString();
        String secondResponse = given().port(8090).when().get(url2).then().assertThat().statusCode(200).extract().asString();

        assertEquals("Hello World", MessageFormat.format("{0} {1}", firstResponse, secondResponse));
    }
}
