package com.woezelmann.zuul.dynamicrouting;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ZuulDynamicRoutingApplication.class)
@WebIntegrationTest
public abstract class AbstractTest  {
    private static String JWT_DEFAULT_ROUTING = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHQiOjIsImF1dCI6Ik5PUk1BTCIsImdlbiI6Ik1BTEUiLCJjbm8iOiIzMDI0ODI3NzQ0Iiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl0sImNvdSI6IkRFIiwiZXhwIjoxNDc4NjE3MDg2LCJhdXRob3JpdGllcyI6WyJET0lfVVNFUiJdLCJqdGkiOiI0NTZhOTg2YS1iZmVmLTQzNzAtODliOS1mZTI1NWJiYjdjN2MiLCJjbGllbnRfaWQiOiJ3ZWJjbGllbnQifQ.JZ-RJcQBnR7x26mL80dfBHu0zlAE-MKRHHaJhm2ebUc";
    private static String JWT_ALTERNATIVE_ROUTING = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHQiOjIsImF1dCI6Ik5PUk1BTCIsImdlbiI6Ik1BTEUiLCJjbm8iOiIxMjM0NTYiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiY291IjoiREUiLCJleHAiOjE0Nzg2MTcwODYsImF1dGhvcml0aWVzIjpbIkRPSV9VU0VSIl0sImp0aSI6IjQ1NmE5ODZhLWJmZWYtNDM3MC04OWI5LWZlMjU1YmJiN2M3YyIsImNsaWVudF9pZCI6IndlYmNsaWVudCJ9.XWzaY6j_vi54IZuJik_p00LCHLO0N_2pH-SIbqbuFCU";

    private static TestRestTemplate template = new TestRestTemplate();

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(8090);

    protected void callSimple() {
        template.exchange("http://localhost:8080/simple", HttpMethod.GET, new HttpEntity(new HttpHeaders()), String.class);
    }

    protected void callDefault() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT_DEFAULT_ROUTING);
        template.exchange("http://localhost:8080/routed", HttpMethod.GET, new HttpEntity(headers), String.class);
    }

    protected void callAlternative() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT_ALTERNATIVE_ROUTING);
        template.exchange("http://localhost:8080/routed", HttpMethod.GET, new HttpEntity(headers), String.class);
    }
}
