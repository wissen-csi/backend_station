package com.station.project.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.station.project.application.services.UserAuthService;
import com.station.project.domain.model.UserAuth;
import com.station.project.infrastructure.components.JwtAuthFilter;

import jakarta.persistence.EntityNotFoundException;
@Configuration
@EnableWebSecurity 
@EnableMethodSecurity 
public class SecurityConfig {

    private final UserAuthService userAuthService;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(
            UserAuthService userAuthService,
            JwtAuthFilter jwtAuthFilter) {
        this.userAuthService = userAuthService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable) 
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/mqtt/auth").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll() 
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean 
    public UserDetailsService userDetailsService() {
        return userName -> {
            final UserAuth user;
            try {
                user = userAuthService.findByUserName(userName);
            } catch (EntityNotFoundException exception) {
                throw new org.springframework.security.core.userdetails.UsernameNotFoundException(
                        "User not found", exception);
            }
            
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.userName())
                    .password(user.password())
                    .roles(user.role().name())
                    .build();
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}