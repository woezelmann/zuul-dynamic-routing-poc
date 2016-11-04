package com.woezelmann.zuul.dynamicrouting.filter;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class HeaderDependentRoutingFilter extends ZuulFilter {

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
        return Boolean.valueOf(RequestContext.getCurrentContext().getRequest().getHeader("X-Alternative-Routing"));
    }

    @Override
    public Object run() {
        try {
            DynamicStringProperty alternativeUrl = DynamicPropertyFactory.getInstance().getStringProperty("zuul.routes.myservice.alturl", null);
            RequestContext.getCurrentContext().setRouteHost(new URL(alternativeUrl.get()));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
