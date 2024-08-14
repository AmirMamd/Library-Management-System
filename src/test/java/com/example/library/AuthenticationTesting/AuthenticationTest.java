package com.example.library.AuthenticationTesting;

import com.example.library.Common.ApplicationRoles;
import com.example.library.Common.Enums;
import com.example.library.Controllers.AuthenticationController;
import com.example.library.DTOs.Login.LoginDTO;
import com.example.library.DTOs.Login.LoginResponseDTO;
import com.example.library.DTOs.RegisterDTO;
import com.example.library.Security.CustomUserDetailsService;
import com.example.library.Security.JWTAuthenticationFilter;
import com.example.library.Security.JWTGenerator;
import com.example.library.Service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.springframework.security.test.context.support.WithMockUser;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthenticationController.class)
public class AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private static JWTGenerator jwtGenerator;

    @MockBean
    private static CustomUserDetailsService customUserDetailsService;


    @Test
    @WithMockUser
    public void registerPatron_ShouldReturnSuccessResponse() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("testUser", "test@example.com", "test","password123", "+123456789", "Country", "Address");
        LoginResponseDTO responseDTO = new LoginResponseDTO("token", 1L, Enums.StatusResponse.Success, "Patron registered successfully");

        when(authenticationService.registerPatron(any(RegisterDTO.class), anyList())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/Authentication/RegisterPatron")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.message").value("Patron registered successfully"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    public void registerLibrarian_ShouldReturnSuccessResponse() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("librarian", "librarian@example.com", "librarian", "password123", "+123456789", "Country", "Address");
        LoginResponseDTO responseDTO = new LoginResponseDTO("token", 2L, Enums.StatusResponse.Success, "Librarian registered successfully");

        when(authenticationService.registerLibrarian(any(RegisterDTO.class), anyList())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/Authentication/RegisterLibrarian")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.message").value("Librarian registered successfully"))
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    public void registerPatron_MissingFields_ShouldReturnBadRequest() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO(null, "test@example.com", "test", "password123", "+123456789", "Country", "Address");
        LoginResponseDTO responseDTO = new LoginResponseDTO(null, null, Enums.StatusResponse.Failed, "User registration failed");

        when(authenticationService.registerPatron(any(RegisterDTO.class), anyList())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/Authentication/RegisterPatron")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("User registration failed"));
    }


    @Test
    public void registerPatron_ShouldReturnBadRequest() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("testUser", "test@example.com", "test", "", "+123456789", "Country", "Address");
        LoginResponseDTO responseDTO = new LoginResponseDTO(null, null, Enums.StatusResponse.Failed, "User registration failed");

        when(authenticationService.registerPatron(any(RegisterDTO.class), anyList())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/Authentication/RegisterPatron")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("User registration failed"));
    }

    @Test
    @WithMockUser
    public void login_ShouldReturnSuccessResponse() throws Exception {
        LoginDTO loginDTO = new LoginDTO("username", "password123");
        LoginResponseDTO responseDTO = new LoginResponseDTO("token", 1L, Enums.StatusResponse.Success, "Login Successful");

        when(authenticationService.loginPatron(any(LoginDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/Authentication/Login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value("Success"))
                .andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.message").value("Login Successful"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void registerPatron_DuplicateUsername_ShouldReturnBadRequest() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("duplicateUser", "duplicate@example.com", "duplicateUser", "password123", "+123456789", "Country", "Address");
        LoginResponseDTO responseDTO = new LoginResponseDTO(null, null, Enums.StatusResponse.Failed, "Username or email already in use");

        when(authenticationService.registerPatron(any(RegisterDTO.class), anyList())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/Authentication/RegisterPatron")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("Username or email already in use"));
    }

    @Test
    public void accessProtectedEndpointWithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void login_InvalidCredentials_ShouldReturnBadRequest() throws Exception {
        LoginDTO loginDTO = new LoginDTO("username", "wrongpassword");
        LoginResponseDTO responseDTO = new LoginResponseDTO(null, null, Enums.StatusResponse.Failed, "Login failed");

        when(authenticationService.loginPatron(any(LoginDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/Authentication/Login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("Login failed"));
    }


    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {
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

    }

}
