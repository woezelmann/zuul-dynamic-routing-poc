package com.woezelmann.zuul.dynamicrouting.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

@Component
public class HeaderDependentRoutingFilter extends ZuulFilter {
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1000;
    }

    @Override
    public boolean shouldFilter() {
        if (!shouldPathBeConcideredForRerouting()) {
            return false;
        }

        if (!shouldCustomerBeRerouted()) {
            return false;
        }

        return true;
    }

    private boolean shouldPathBeConcideredForRerouting() {
        String path = DynamicPropertyFactory.getInstance().getStringProperty("zuul.routes.routedservice.path", null).get();
        return antPathMatcher.match(path, RequestContext.getCurrentContext().getRequest().getRequestURI());
    }

    @Override
    public Object run() {
        try {
            RequestContext.getCurrentContext().setRouteHost(new URL(DynamicPropertyFactory.getInstance().getStringProperty("zuul.routes.routedservice.alturl", null).get()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean shouldCustomerBeRerouted() {
        try {
            String authorization = RequestContext.getCurrentContext().getRequest().getHeader("Authorization");
            authorization = authorization.substring(7);
            String[] parts = authorization.split("\\.");

            String decode = new String (Base64.decode(parts[1]));
            Map<String, Object> map = objectMapper.readValue(decode, Map.class);
            return map.get("cno").equals("123456");
        } catch (IOException e) {
            return false;
        }
    }
}
