package com.logitrack.logitrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final KeycloakJwtConverter keycloakJwtConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/home",
                                "/public/**",
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/actuator/**"
                        ).permitAll()
                        
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/products/**", "/api/carriers/**", "/api/suppliers/**", "/api/warehouses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**", "/api/carriers/**", "/api/suppliers/**", "/api/warehouses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**", "/api/carriers/**", "/api/suppliers/**", "/api/warehouses/**").hasRole("ADMIN")
                        
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        
                        .requestMatchers("/api/purchase-orders/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/sales-orders/all").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/sales-orders/*/reserve", "/api/sales-orders/*/ship", "/api/sales-orders/*/deliver").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/suppliers/**", "/api/carriers/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        
                        .requestMatchers(HttpMethod.POST, "/api/sales-orders/**").hasAnyRole("ADMIN", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER", "CLIENT")
                        
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

   
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(keycloakJwtConverter);
        converter.setPrincipalClaimName("preferred_username");
        return converter;
    }

}
