package com.interswitch.StyleMe.security;

import com.interswitch.StyleMe.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
public class ApplicationSecurityConfig {
    private final UnAuthorizedEntryPoint  unAuthorizedEntryPoint;

    public ApplicationSecurityConfig(UnAuthorizedEntryPoint unAuthorizedEntryPoint) {
        this.unAuthorizedEntryPoint = unAuthorizedEntryPoint;
    }


    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeHttpRequests(authorize -> {
                    try {
                        authorize.antMatchers("/**/auth/**", "/api/v1/auth/", "/v2/api-docs/**", "/api/v1/confirm/**",
                                "/swagger-ui/**", "/swagger-resources/**", "/swagger-ui-html", "/webjars/**").permitAll()
                                .antMatchers("/customError").permitAll()
                                .antMatchers("/access-denied").permitAll()
                                .anyRequest().authenticated()
                                .and()
                                .oauth2Login(Customizer.withDefaults())
                                .exceptionHandling().authenticationEntryPoint(unAuthorizedEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler())
                                .and()
                                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        http.addFilterBefore(jwtAuthenticationFilterBean(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilterBean(){
        return new JwtAuthenticationFilter();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
}
