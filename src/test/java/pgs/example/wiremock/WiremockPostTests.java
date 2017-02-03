package pgs.example.wiremock;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.restassured.mapper.ObjectMapperType;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import pgs.example.wiremock.model.SampleRequest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

/**
 * Created by pkrawiec on 1/30/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WiremockPostTests {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Before
    public void setUp() throws Exception {
        baseURI = "http://localhost:8089";
        String url = "/pgs/.*";

        givenThat(post(urlMatching(url)).atPriority(9)
                .willReturn(aResponse()
                        .withStatus(400)));
    }

    @Test
    public void postResponse_success() {
        String url = "/pgs/resource/1";
        String sampleResponse = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Someone\",\n" +
                "  \"city\": \"Somewhere\"\n" +
                "}";
        SampleRequest sampleRequest = new SampleRequest();
        sampleRequest.setId(1);

        givenThat(post(urlEqualTo(url)).withRequestBody(containing("\"id\":1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(sampleResponse)));

        given().body(sampleRequest, ObjectMapperType.GSON).when().post(url).then().assertThat().statusCode(200).and().assertThat().body(Matchers.equalTo(sampleResponse));
    }
}
