package example.wiremock.service;

import example.wiremock.model.SampleResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;

/**
 * Created by pkrawiec on 3/1/2017.
 */
@Service
public class SampleService {

    @Value("${serviceUrl}")
    private String serviceUrl;

    public SampleResponse getSampleResponse() {

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(MessageFormat.format(serviceUrl, "test/resource"), SampleResponse.class);
    }
}
