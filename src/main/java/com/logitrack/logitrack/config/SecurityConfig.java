package com.logitrack.logitrack.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Désactiver CSRF (requis pour les API REST stateless)
                .csrf(csrf -> csrf.disable())

                // 2. Définir la politique de session sur STATELESS (pour Basic Auth/JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. Activer Basic Auth (Ceci insère le BasicAuthenticationFilter)
                .httpBasic(Customizer.withDefaults())

                // 4. Configuration des règles d'Autorisation (Qui peut accéder à quoi)
                .authorizeHttpRequests(auth -> auth
                        // Règles spécifiques
                        .requestMatchers("/","/api/auth/**").permitAll()
                        // Routes ADMIN : Accès uniquement par ADMIN
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/products/**",
                                "/api/carriers/**"
                                ,"/api/suppliers/**"
                                ,"/api/warehouses/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**"
                                ,"/api/carriers/**"
                                ,"/api/suppliers/**"
                                ,"/api/warehouses/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**"
                                ,"/api/carriers/**"
                                ,"/api/suppliers/**"
                                ,"/api/warehouses/**").hasRole("ADMIN")


                        // Routes WAREHOUSE_MANAGER : Gère l'inventaire et les expéditions
                        .requestMatchers("/api/purchase-orders/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/sales-orders/all").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/sales-orders/**/reserve",
                                "/api/sales-orders/**/ship",
                                "/api/sales-orders/**/deliver")
                        .hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.GET,"/api/suppliers/**","/api/carriers/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")



                        // Routes CLIENT : Peut créer des commandes (POST) et consulter (GET)
                        .requestMatchers(HttpMethod.POST, "/api/sales-orders/**").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER", "CLIENT")

                        // Toute autre requête nécessite au moins une authentification valide
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}




