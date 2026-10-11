package com.dem_ams.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(request -> request
                    // probably not a good thing to do, but it should work
                    .requestMatchers(HttpMethod.POST, "/api/payments/**").hasAuthority("payments:create")
                    .requestMatchers(HttpMethod.PUT, "/api/payments/**").hasAuthority("payments:update")
                    .requestMatchers(HttpMethod.GET, "/api/payments/**").hasAuthority("payments:read")
                    .requestMatchers(HttpMethod.DELETE, "/api/payments/**").hasAuthority("payments:delete")
                    .requestMatchers("/api/tenants/**").hasAnyRole("ADMIN", "TENANT")
                    .requestMatchers("/api/users/*/info").hasRole("ADMIN")
                    .requestMatchers("/api/users/**").authenticated()
                    .requestMatchers("/api/landlords/**").hasAnyRole("ADMIN", "LANDLORD")
                    // TODO: use hasAuthority for invoices instead
                    .requestMatchers("/api/invoices/**").hasAnyRole("ADMIN", "LANDLORD")
                    .requestMatchers(HttpMethod.POST, "/api/properties/**").hasAuthority("properties:create")
                    .requestMatchers(HttpMethod.PUT, "/api/properties/**").hasAuthority("properties:update")
                    .requestMatchers(HttpMethod.DELETE, "/api/properties/**").hasAuthority("properties:delete")
                    .requestMatchers(HttpMethod.GET, "/api/properties/**").hasAuthority("properties:read")
                    .requestMatchers("/api/public/**").permitAll()
                    .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Profile("test")
    PasswordEncoder passwordEncoderTest() {
        String idForEncode = "bcrypt";
        Map encoders = new HashMap<>();
        encoders.put(idForEncode, new BCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());

        PasswordEncoder passwordEncoder =
                new DelegatingPasswordEncoder(idForEncode, encoders);
        return passwordEncoder;
    }
}
