package com.example.library.Security;

import com.example.library.Common.ApplicationRoles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTGenerator jwtGenerator;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JWTGenerator jwtGenerator, CustomUserDetailsService customUserDetailsService) {
        this.jwtGenerator = jwtGenerator;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authRequests -> {
                    authRequests.requestMatchers("/api/Authentication/**").permitAll();
                    authRequests.requestMatchers(HttpMethod.GET, "/api/books/**").hasAuthority(ApplicationRoles.View_Books)
                            .requestMatchers(HttpMethod.POST, "/api/books/**").hasAuthority(ApplicationRoles.Add_Books)
                            .requestMatchers(HttpMethod.PUT, "/api/books/**").hasAuthority(ApplicationRoles.Edit_Books)
                            .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasAuthority(ApplicationRoles.Delete_Books);
                    authRequests.requestMatchers(HttpMethod.GET, "/api/patrons/**").hasAuthority(ApplicationRoles.View_Patrons)
                            .requestMatchers(HttpMethod.POST, "/api/books/**").hasAuthority(ApplicationRoles.Add_Patrons)
                            .requestMatchers(HttpMethod.PUT, "/api/books/**").hasAuthority(ApplicationRoles.Edit_Patrons)
                            .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasAuthority(ApplicationRoles.Delete_Patrons);
                    authRequests.requestMatchers("/api/borrow/**").hasAuthority(ApplicationRoles.Borrow_Book);
                    authRequests.requestMatchers("/api/return/**").hasAuthority(ApplicationRoles.Return_Book);
                    authRequests.requestMatchers("/**").permitAll();
                })
                .httpBasic(AbstractHttpConfigurer::disable);
        http.addFilterBefore(new JWTAuthenticationFilter(jwtGenerator, customUserDetailsService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
