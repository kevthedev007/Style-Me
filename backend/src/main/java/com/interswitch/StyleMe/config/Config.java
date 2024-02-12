package com.interswitch.StyleMe.config;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.http.HttpMethod.*;

@Configuration
public class Config {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

     @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        //First way of configuring core
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(false);
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-TYpe", "Accept",
                "Access-Control-Allow-Headers", "Access-Control-Allow-Origin", "Access-Control-Request-Method",
                "Access-Control-Request-Headers", "Origin"));
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:8080/", "google.com", "facebook.com","http://localhost:3000/"));
        corsConfiguration.setAllowedMethods(List.of(
                HEAD.name(), GET.name(), POST.name(), PUT.name(), PATCH.name(), DELETE.name()
        ));
        corsConfiguration.setMaxAge(3600L);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
