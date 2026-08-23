package com.sai.device;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final DeviceIdInterceptor deviceIdInterceptor;

    WebMvcConfig(DeviceIdInterceptor deviceIdInterceptor) {
        this.deviceIdInterceptor = deviceIdInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(deviceIdInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/error",
                        "/doc.html", // knife4j 文档首页
                        "/webjars/**", // knife4j 静态资源
                        "/v3/api-docs/**", // OpenAPI 文档
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/favicon.ico");
    }
}
