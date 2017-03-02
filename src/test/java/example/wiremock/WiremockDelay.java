package example.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

/**
 * Created by pkrawiec on 3/2/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WiremockDelay {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Before
    public void setUp() throws Exception {
        baseURI = "http://localhost:8089";
        WireMock.setGlobalFixedDelay(Math.toIntExact(TimeUnit.SECONDS.toMillis(30)));
    }

    @Test
    public void timeout() {
        String url = "/test/timeout";
        givenThat(get(urlEqualTo("/test/timeout")).willReturn(aResponse().withStatus(200).withFixedDelay(Math.toIntExact(TimeUnit.SECONDS.toMillis(5)))));

        given().when().get(url).then().assertThat().statusCode(200);
    }
}
