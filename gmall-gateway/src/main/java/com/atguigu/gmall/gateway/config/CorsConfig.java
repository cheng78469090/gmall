package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/3 11:45
 * @Description:
 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter(){
        // 初始化CORS配置对象
        CorsConfiguration config = new CorsConfiguration();
        // 允许的域,不要写*，否则cookie就无法使用了
        config.addAllowedOrigin("http://localhost:1000");
        // 允许的头信息
        config.addAllowedHeader("*");
        // 允许的请求方式
        config.addAllowedMethod("*");
        // 是否允许携带Cookie信息
        config.setAllowCredentials(true);

        // 添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(corsConfigurationSource);

       /* //初始化cros配置对象
        CorsConfiguration config = new CorsConfiguration();
        //配置允许的域，就是所跨的域，你要跨的哪个域，不能使用*，因为使用后就无法使用cookie
        config.addAllowedOrigin("http://127.0.0.1:8888");
        //允许的头信息
        config.addAllowedHeader("*");
        // 允许的请求方式有哪些，设置成全部
        config.addAllowedMethod("*");
        //是否可以携带cookie
        config.setAllowCredentials(true);
        //添加映射路径，我们要过滤所有的请求
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",config);
        return new CorsWebFilter(configurationSource);*/
    }
}
