package com.dem_ams.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Bean
    SecurityContextLogoutHandler securityContextLogoutHandler() {
        return new SecurityContextLogoutHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(request -> request
                    // probably not a good thing to do, but it should work
                    .requestMatchers(HttpMethod.POST, "/api/payments/**").hasAuthority("payments:create")
                    .requestMatchers(HttpMethod.PUT, "/api/payments/**").hasAuthority("payments:update")
                    .requestMatchers(HttpMethod.GET, "/api/payments/**").hasAuthority("payments:read")
                    .requestMatchers(HttpMethod.DELETE, "/api/payments/**").hasAuthority("payments:delete")
                    .requestMatchers("/api/tenants/me/**").hasRole("TENANT")
                    .requestMatchers("/api/tenants/**").hasAnyRole("ADMIN", "TENANT")
                    .requestMatchers("/api/users/**").authenticated()
                    .requestMatchers("/api/landlords/**").hasAnyRole("ADMIN", "LANDLORD")
                    // TODO: use hasAuthority for invoices instead
                    .requestMatchers("/api/invoices/**").hasAnyRole("ADMIN", "LANDLORD")
                    .requestMatchers(HttpMethod.POST, "/api/properties/**").hasAuthority("properties:create")
                    .requestMatchers(HttpMethod.PUT, "/api/properties/**").hasAuthority("properties:update")
                    .requestMatchers(HttpMethod.GET, "/api/properties/**").hasAuthority("properties:read")
                    .requestMatchers(HttpMethod.DELETE, "/api/properties/**").hasAuthority("properties:delete")
                    .requestMatchers("/api/leases/**").hasAnyRole("ADMIN", "LANDLORD")
                    .anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                )
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
//        wont work if db user passwords dont have bcrypt prefixes
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        return new BCryptPasswordEncoder();
        String idForEncode = "bcrypt";
        Map encoders = new HashMap<>();
        encoders.put(idForEncode, new BCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());

        PasswordEncoder passwordEncoder =
                new DelegatingPasswordEncoder(idForEncode, encoders);
        return passwordEncoder;
    }

//    @Bean
//    @Profile("test")
//    PasswordEncoder passwordEncoderTest() {
//        String idForEncode = "bcrypt";
//        Map encoders = new HashMap<>();
//        encoders.put(idForEncode, new BCryptPasswordEncoder());
//        encoders.put("noop", NoOpPasswordEncoder.getInstance());
//
//        PasswordEncoder passwordEncoder =
//                new DelegatingPasswordEncoder(idForEncode, encoders);
//        return passwordEncoder;
//    }
}
