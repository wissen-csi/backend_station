package com.station.project.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.station.project.application.port.out.UserAuthRepositoryPort;
import com.station.project.domain.model.UserAuth;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserAuthRepositoryPort repositoryPort;

    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable) 
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll() 
                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean 
    public UserDetailsService userDetailsService() {
        return userName -> {
            final UserAuth user = repositoryPort.findByUserName(userName)
                    .orElseThrow(() -> new UsernameNotFoundException("User name not found: " + userName));
            
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.userName())
                    .password(user.password())
                    .roles(user.role().toString())
                    .build();
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService());
    
        authProvider.setPasswordEncoder(passwordEncoder());
    
        return authProvider;
    }
}