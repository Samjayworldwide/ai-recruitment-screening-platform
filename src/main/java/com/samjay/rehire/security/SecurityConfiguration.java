package com.samjay.rehire.security;

import com.samjay.rehire.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JWTTokenValidatorFilter jwtTokenValidatorFilter;

    private final JWTTokenGeneratorFilter jwtTokenGeneratorFilter;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) {

        try {
            httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .csrf(AbstractHttpConfigurer::disable)
                    .addFilterAfter(jwtTokenGeneratorFilter, BasicAuthenticationFilter.class)
                    .addFilterBefore(jwtTokenValidatorFilter, BasicAuthenticationFilter.class)
                    .authorizeHttpRequests(requests ->
                            requests
                                    .requestMatchers("/api/v1/signup",
                                            "/api/v1/resend-token",
                                            "/api/v1/verify-token",
                                            "/api/v1/signin",
                                            "/api/v1/submit-application",
                                            "/api/v1/fetch-application-form")
                                    .permitAll()
                                    .requestMatchers("/api/v1/create-job",
                                            "/api/v1/fetch-organization-jobs",
                                            "/api/v1/fetch-job-applicants",
                                            "/api/v1/filter",
                                            "/api/v1/add-to-cart",
                                            "/api/v1/fetch-cart-candidates",
                                            "/api/v1/remove-from-cart",
                                            "/api/v1/generate-email",
                                            "/api/v1/send-bulk-emails",
                                            "/api/v1/move-candidates")
                                    .authenticated()
                    )
                    .formLogin(AbstractHttpConfigurer::disable)
                    .httpBasic(AbstractHttpConfigurer::disable);

            return httpSecurity.build();

        } catch (Exception e) {

            throw new ApplicationException("An unexpected error occured", HttpStatus.BAD_REQUEST);

        }

    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();

    }
}