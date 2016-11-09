package com.woezelmann.zuul.dynamicrouting;

import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


public class ZuulDynamicRoutingTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {
        stubFor(get(urlMatching(".*")).willReturn(aResponse().withStatus(200)));
    }

    @Test
    public void routesToSimple() throws Exception {
        callSimple();

        verify(getRequestedFor(urlEqualTo("/simple")));
    }

    @Test
    public void routesToDefault() throws Exception {
        callDefault();

        verify(getRequestedFor(urlEqualTo("/default")));

    }

    @Test
    public void routesToAlternative() throws Exception {
        callAlternative();

        verify(getRequestedFor(urlEqualTo("/alternative")));
    }
}