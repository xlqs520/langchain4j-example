package org.xlqs.com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                    ClientRegistrationRepository clientRegistrationRepository) throws Exception {

        DefaultOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
                        "/oauth2/authorization");

        defaultResolver.setAuthorizationRequestCustomizer(customizer ->
                customizer.redirectUri("https://xlqs.com:8080/login/oauth2/code/github")
        );

        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/public/**", "/**",  "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestResolver(defaultResolver)
                        )
                        .failureHandler((request, response, exception) ->
                                response.sendRedirect("/login?error&continue"))
                        .defaultSuccessUrl("/health", true)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/oauth2/authorization/github"))
                );

        return http.build();
    }

}
