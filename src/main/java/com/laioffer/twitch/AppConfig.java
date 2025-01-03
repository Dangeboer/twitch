package com.laioffer.twitch;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.sql.DataSource;

@Configuration // 提供配置的，其实改成service也可以，但一般不会这样
public class AppConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { // 过滤，密码对不对、在不在黑名单等等
        http
                .csrf().disable()
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .requestMatchers(HttpMethod.GET, "/", "/index.html", "/*.json", "/*.png", "/static/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/login", "/register", "/logout").permitAll()
                                .requestMatchers(HttpMethod.GET, "/recommendation", "/game", "/search").permitAll() // 没有登陆的时候也能浏览
                                .anyRequest().authenticated()
                )
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // 强制return UNAUTHORIZED告诉前端
                .and()
                .formLogin()
                .successHandler((req, res, auth) -> res.setStatus(HttpStatus.NO_CONTENT.value())) // login成功
                .failureHandler(new SimpleUrlAuthenticationFailureHandler()) // login失败
                .and()
                .logout()
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT));
        return http.build();
    }

    @Bean
    UserDetailsManager users(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    PasswordEncoder passwordEncoder() { // 用于给密码加密，比较简单的密码会被GPU快速破解（暴力解）
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}