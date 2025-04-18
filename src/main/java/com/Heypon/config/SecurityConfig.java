package com.Heypon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 放行的公共接口（直接列出路径，`antMatchers` 支持可变参数）
    public static final String[] PUBLIC_URLS = {
            "/user/**",
            "/chart/**",
            "/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF
                .csrf().disable()
                // 配置会话策略（无状态）
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 配置请求权限（旧版语法：`authorizeRequests()` + `antMatchers()`）
                .authorizeRequests(auth -> auth
                        // 公共接口放行（直接传入多个字符串，无需数组）
                        .antMatchers(PUBLIC_URLS).permitAll()
                        // 其他请求需认证
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}