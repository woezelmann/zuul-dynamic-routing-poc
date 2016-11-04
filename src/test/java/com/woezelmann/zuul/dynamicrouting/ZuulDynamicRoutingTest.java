package com.woezelmann.zuul.dynamicrouting;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ZuulDynamicRoutingApplication.class)
@WebIntegrationTest
public class ZuulDynamicRoutingTest {

    private TestRestTemplate template = new TestRestTemplate();

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(8090);

    @Before
    public void setUp() throws Exception {
        stubFor(get(urlMatching(".*")).willReturn(aResponse().withStatus(200)));
    }

    @Test
    public void routesToDefault() throws Exception {
        template.exchange("http://localhost:8080", HttpMethod.GET, new HttpEntity(new HttpHeaders()), String.class);

        verify(getRequestedFor(urlEqualTo("/green/")));

    }

    @Test
    public void routesToAlternative() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Alternative-Routing", "true");
        template.exchange("http://localhost:8080", HttpMethod.GET, new HttpEntity(headers), String.class);

        verify(getRequestedFor(urlEqualTo("/blue/")));

    }
}