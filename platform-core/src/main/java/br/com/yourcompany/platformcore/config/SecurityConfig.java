package br.com.yourcompany.platformcore.config;

import br.com.yourcompany.platformcore.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter; // O "assistente"

    @Autowired
    private AuthenticationProvider authenticationProvider; // O "provedor" (agora vindo do ApplicationConfig)

    // O "Guarda" (SecurityFilterChain)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            
            .authorizeHttpRequests(authz -> authz
                // Libera endpoints de auth (login e registro)
                .requestMatchers("/api/v1/auth/**").permitAll()
                
                // Libera o endpoint do WebSocket
                .requestMatchers("/ws/connect/**").permitAll() 
                
                // Libera endpoints de monitoramento do Actuator
                .requestMatchers("/actuator/**").permitAll()
                
                // Protege todo o resto
                .anyRequest().authenticated() 
            )
            
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Usa o provedor que injetamos (do ApplicationConfig)
            .authenticationProvider(authenticationProvider)
            
            // Usa o filtro que injetamos
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}