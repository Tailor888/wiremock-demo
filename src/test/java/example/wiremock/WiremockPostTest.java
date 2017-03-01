package example.wiremock;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import example.wiremock.model.SampleRequest;
import example.wiremock.model.SampleResponse;
import io.restassured.mapper.ObjectMapperType;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import wiremock.com.fasterxml.jackson.core.JsonProcessingException;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

/**
 * Created by pkrawiec on 1/30/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WiremockPostTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Before
    public void setUp() throws Exception {
        baseURI = "http://localhost:8089";
        String url = "/test/.*";

        givenThat(post(urlMatching(url)).atPriority(9)
                .willReturn(aResponse()
                        .withStatus(400)));
    }

    @Test
    public void postResponseWithSuccess() {
        String url = "/test/resource/1";
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

    @Test
    public void postResponseWithSuccessForObject() throws JsonProcessingException {
        String url = "/test/resource/1";
        String city = "Somewhere";

        SampleResponse sampleResponse = new SampleResponse();
        sampleResponse.setId(1);
        sampleResponse.setName("Someone");
        sampleResponse.setCity(city);

        SampleRequest sampleRequest = new SampleRequest();
        sampleRequest.setId(1);

        givenThat(post(urlEqualTo(url)).withRequestBody(containing("\"id\":1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(objectMapper.writeValueAsString(sampleResponse))));

        given().body(sampleRequest, ObjectMapperType.GSON).when().post(url).then().assertThat().statusCode(200).and().assertThat().body("city", is(city));
    }

    @Test
    public void postResponseWithSuccessForFile() {
        String url = "/test/resource/1";
        SampleRequest sampleRequest = new SampleRequest();
        sampleRequest.setId(1);

        givenThat(post(urlEqualTo(url)).withRequestBody(containing("\"id\":1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBodyFile("sampleResponse1.json")));

        given().body(sampleRequest, ObjectMapperType.GSON).when().post(url).then().assertThat().statusCode(200).and().assertThat().body("name", is("Someone"));
    }
}
