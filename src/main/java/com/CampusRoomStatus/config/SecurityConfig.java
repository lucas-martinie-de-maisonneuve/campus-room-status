package com.CampusRoomStatus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;

@Configuration
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(
            @Autowired(required = false) ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(
                                        authorizationRequestResolver(clientRegistrationRepository)))
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/google"))
                        .successHandler((request, response, authentication) ->
                                response.sendRedirect("/api/v1/callback"))
                );

        return http.build();
    }

    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {
        var resolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization");
        resolver.setAuthorizationRequestCustomizer(customizer -> customizer.additionalParameters(params -> {
            params.put("access_type", "offline");
            params.put("prompt", "consent");
        }));
        return resolver;
    }
}