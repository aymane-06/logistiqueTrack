package com.logitrack.logitrack.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.logitrack.logitrack.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Bean pour encoder les mots de passe avec BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuration du fournisseur d'authentification par défaut.
     * Utilise votre CustomUserDetailsService et le PasswordEncoder (BCrypt).
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Exposition de l'AuthenticationManager pour l'injecter dans AuthService.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Désactiver CSRF (requis pour les API REST stateless)
                .csrf(AbstractHttpConfigurer::disable)
                // 2. Configuration CORS pour permettre les requêtes du frontend Angular
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Définir la politique de session sur STATELESS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


                // 3. Configuration des règles d'Autorisation
                .authorizeHttpRequests(auth -> auth
                        // Routes publiques (Accueil et Authentification)
                        .requestMatchers("/", "/api/auth/**").permitAll()

                        // Routes ADMIN : Accès uniquement par ADMIN
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/products/**", "/api/carriers/**", "/api/suppliers/**", "/api/warehouses/**","/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**", "/api/carriers/**", "/api/suppliers/**", "/api/warehouses/**","/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**", "/api/carriers/**", "/api/suppliers/**", "/api/warehouses/**","/api/users/**").hasRole("ADMIN")

                        // Routes WAREHOUSE_MANAGER : Gère l'inventaire et les expéditions
                        .requestMatchers("/api/purchase-orders/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/sales-orders/all").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/sales-orders/*/reserve", "/api/sales-orders/*/ship", "/api/sales-orders/*/deliver").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/suppliers/**", "/api/carriers/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")

                        // Routes CLIENT : Peut créer des commandes (POST) et consulter (GET)
                        .requestMatchers(HttpMethod.POST, "/api/sales-orders/**").hasAnyRole("ADMIN","CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER", "CLIENT")

                        // Toute autre requête nécessite au moins une authentification valide
                        .anyRequest().authenticated()
                )

                // 4. Définir le fournisseur d'authentification
                .authenticationProvider(authenticationProvider())

                // 5. Ajouter le filtre JWT avant le filtre de traitement de login standard
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuration CORS pour permettre les requêtes du frontend Angular.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permettre les requêtes depuis le frontend Angular
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Permettre tous les headers
        configuration.setAllowedHeaders(List.of("*"));

        // Permettre toutes les méthodes HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Permettre les cookies/credentials si nécessaire
        configuration.setAllowCredentials(true);

        // Exposer les headers d'autorisation pour JWT
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}