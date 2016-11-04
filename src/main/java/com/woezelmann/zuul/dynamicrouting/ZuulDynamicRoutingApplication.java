package com.woezelmann.zuul.dynamicrouting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication()
@EnableZuulProxy
public class ZuulDynamicRoutingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulDynamicRoutingApplication.class);
    }

}
