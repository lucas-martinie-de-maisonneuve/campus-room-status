package com.CampusRoomStatus.config;
import com.CampusRoomStatus.integration.google.GoogleOAuthTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


@Configuration
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final GoogleOAuthTokenService tokenService;

    public SecurityConfig(
            @Autowired(required = false) ClientRegistrationRepository clientRegistrationRepository,
            GoogleOAuthTokenService tokenService) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.tokenService = tokenService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (tokenService.hasRefreshToken()) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .oauth2Login(AbstractHttpConfigurer::disable);
        } else {
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/**").permitAll()
                    .anyRequest().authenticated())
                .oauth2Login(oauth -> oauth
                    .authorizationEndpoint(endpoint -> endpoint
                        .authorizationRequestResolver(
                            authorizationRequestResolver(clientRegistrationRepository)))
                );
        }
        return http.build();
    }

    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {
        var resolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization");
        resolver.setAuthorizationRequestCustomizer(customizer ->
            customizer.additionalParameters(params -> {
                params.put("access_type", "offline");
                params.put("prompt", "consent");
            })
        );
        return resolver;
    }
}