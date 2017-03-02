package example.wiremock;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import example.wiremock.model.SampleResponse;
import example.wiremock.service.SampleService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import wiremock.com.fasterxml.jackson.core.JsonProcessingException;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.baseURI;
import static org.junit.Assert.assertEquals;

/**
 * Created by pkrawiec on 3/1/2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class WiremockServiceTest {

    @Autowired
    private SampleService sampleService;

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
    public void getResponseWithSuccessForService() throws JsonProcessingException {
        String url = "/test/resource";
        SampleResponse sampleResponse = new SampleResponse();
        sampleResponse.setId(1);
        sampleResponse.setName("Someone");
        sampleResponse.setCity("Somewhere");

        givenThat(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(objectMapper.writeValueAsString(sampleResponse))));

        SampleResponse receivedResponse = sampleService.getSampleResponse();

        //verify(getRequestedFor(urlEqualTo(url + "fail")));
        verify(getRequestedFor(urlEqualTo(url)));
        assertEquals("Somewhere", receivedResponse.getCity());
    }
}
