package com.woezelmann.zuul.dynamicrouting;

import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ZuulDynamicRoutingPerformanceBenchmark extends AbstractTest {

    private static final int REPETITON_COUNT = 1000;

    private List<Long> startTimes = new ArrayList<Long>();
    private List<Long> endTimes = new ArrayList<Long>();

    private static boolean warmedUp = false;

    @Before
    public void setUp() throws Exception {
        stubFor(get(urlMatching(".*")).willReturn(aResponse().withStatus(200)));

        warmUpOnce();

        Thread.sleep(5000);

    }

    private void warmUpOnce() throws InterruptedException {
        if (!warmedUp) {
            ExecutorService executor = Executors.newFixedThreadPool(16);
            System.out.println("----- WARM UP -----");

            for (int i = 0; i < 50000; i++) {
                executor.submit(this::callSimple);
                executor.submit(this::callDefault);
                executor.submit(this::callAlternative);

            }

            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.MINUTES);

            System.out.println("----- WARM UP FINISHED -----");

            wireMockRule.resetRequests();

            warmedUp = true;
        }
    }

    @Test
    public void testSimplePerformance() throws Exception {
        withTimer(this::callSimple);

        verify(REPETITON_COUNT, getRequestedFor(urlEqualTo("/simple")));

        createStatistics("SIMPLE");
    }

    @Test
    public void testDefaultRoutingPerformance() throws Exception {
        withTimer(this::callDefault);

        verify(REPETITON_COUNT, getRequestedFor(urlEqualTo("/default")));

        createStatistics("DEFAULT");
    }

    @Test
    public void testAlternativeRoutingPerformance() throws Exception {
        withTimer(this::callAlternative);

        verify(REPETITON_COUNT, getRequestedFor(urlEqualTo("/alternative")));

        createStatistics("ALTERNATIVE");
    }

    private void withTimer(Caller caller) {
        for (int i = 0; i < REPETITON_COUNT; i++) {
            startTimes.add(System.nanoTime());
            caller.doIt();
            endTimes.add(System.nanoTime());
        }
    }

    private void createStatistics(String name) {
        List<LoggedRequest> loggedRequests = wireMockRule.findAll(RequestPatternBuilder.allRequests());

        long time = 0;
        for (int i = 0; i < REPETITON_COUNT; i++) {
            time += endTimes.get(i) - startTimes.get(i);
        }

        time = TimeUnit.NANOSECONDS.toMillis(time);

        System.out.println("----- " + name + " -----");
        System.out.println("OverallTime: " + (time) + " ms");
        System.out.println("AverageTime: " + ((double)time / REPETITON_COUNT) + "ms");
        System.out.println();
    }

    private interface Caller {
        void doIt();
    }

}
