package com.example.library.PatronTests;

import com.example.library.Common.ApplicationRoles;
import com.example.library.Common.Enums;
import com.example.library.Controllers.PatronController;
import com.example.library.DTOs.Patron.AllPatronsResponseDTO;
import com.example.library.DTOs.Patron.PatronDTO;
import com.example.library.DTOs.Patron.PatronResponseDTO;
import com.example.library.DTOs.RegisterDTO;
import com.example.library.DTOs.ResponseDTO;
import com.example.library.Security.CustomUserDetailsService;
import com.example.library.Security.JWTAuthenticationFilter;
import com.example.library.Security.JWTGenerator;
import com.example.library.Service.PatronService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PatronController.class)
public class PatronTest {

    @MockBean
    private PatronService patronService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private static JWTGenerator jwtGenerator;

    @MockBean
    private static CustomUserDetailsService customUserDetailsService;


    @Test
    @WithMockUser(roles = "View_Patrons")
    public void getPatronById_ShouldReturnSuccessResponse() throws Exception {
        PatronResponseDTO patronResponseDTO = new PatronResponseDTO(
                1L, "John Doe", "123 Main St", "john.doe@example.com",
                "johndoe", "password123", "1234567890", "USA", false,
                Enums.StatusResponse.Success, "Patron found");

        when(patronService.getPatronById(anyLong())).thenReturn(patronResponseDTO);

        mockMvc.perform(get("/api/patrons/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.message").value("Patron found"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }


    @Test
    @WithMockUser(roles = "View_Patrons")
    public void getPatronById_ShouldReturnBadRequestOnFailure() throws Exception {
        PatronResponseDTO patronResponseDTO = new PatronResponseDTO(
                null, null, null, null, null, null, null, null, false,
                Enums.StatusResponse.Failed, "Patron not found");

        when(patronService.getPatronById(anyLong())).thenReturn(patronResponseDTO);

        mockMvc.perform(get("/api/patrons/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("Patron not found"));
    }


    @Test
    @WithMockUser(roles = "Edit_Patrons")
    public void editPatronById_ShouldReturnSuccessResponse() throws Exception {
        PatronDTO patronDTO = new PatronDTO(1L, "John Doe", "123 Main St", "john.doe@example.com",
                "johndoe", "password123", "1234567890", "USA", false);

        ResponseDTO responseDTO = new ResponseDTO(Enums.StatusResponse.Success, "Patron updated successfully");

        when(patronService.editPatronById(anyLong(), any(PatronDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/patrons/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(patronDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.message").value("Patron updated successfully"));
    }
    @Test
    @WithMockUser(roles = "Edit_Patrons")
    public void editPatronById_ShouldReturnBadRequestOnFailure() throws Exception {
        PatronDTO patronDTO = new PatronDTO(1L, "John Doe", "123 Main St", "john.doe@example.com",
                "johndoe", "password123", "1234567890", "USA", false);

        ResponseDTO responseDTO = new ResponseDTO(Enums.StatusResponse.Failed, "Failed to update patron");

        when(patronService.editPatronById(anyLong(), any(PatronDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/patrons/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(patronDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("Failed to update patron"));
    }

    @Test
    @WithMockUser(roles = "View_Patrons")
    public void getPatrons_ShouldReturnList() throws Exception {
        PatronDTO patronDTO = new PatronDTO(1L, "John Doe", "123 Main St", "john.doe@example.com",
                "johndoe", "password123", "1234567890", "USA", false);

        AllPatronsResponseDTO allPatronsResponseDTO = new AllPatronsResponseDTO(
                Enums.StatusResponse.Success, "Patrons found", Collections.singletonList(patronDTO));

        when(patronService.getAllPatrons()).thenReturn(allPatronsResponseDTO);

        mockMvc.perform(get("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.message").value("Patrons found"))
                .andExpect(jsonPath("$.patrons[0].fullName").value("John Doe"));
    }

    @Test
    @WithMockUser(roles = "Edit_Patrons")
    public void deletePatron_ShouldReturnSuccessResponse() throws Exception {
        ResponseDTO responseDTO = new ResponseDTO(Enums.StatusResponse.Success, "Patron deleted successfully");

        when(patronService.deletePatronById(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(delete("/api/patrons/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.message").value("Patron deleted successfully"));
    }


    @Test
    @WithMockUser(roles = "Add_Patrons")
    public void addPatron_ShouldReturnSuccessResponse() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("John Doe", "123 Main St", "john.doe@example.com",
                "johndoe", "password123", "1234567890", "USA");

        ResponseDTO responseDTO = new ResponseDTO(Enums.StatusResponse.Success, "Patron added successfully");

        when(patronService.addPatron(any(RegisterDTO.class), any(List.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.message").value("Patron added successfully"));
    }

    @Test
    @WithMockUser(roles = "Add_Patrons")
    public void addPatron_ShouldReturnBadRequestOnFailure() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO(null, null, "john.doe@example.com",
                "johndoe", "password123", "1234567890", "USA");

        ResponseDTO responseDTO = new ResponseDTO(Enums.StatusResponse.Failed, "Failed to create patron");

        when(patronService.addPatron(any(RegisterDTO.class), any(List.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("Failed to create patron"));
    }

    @Test
    @WithMockUser(roles = "Unauthorized_Role")
    public void getPatronById_ShouldReturnForbiddenForUnauthorizedRole() throws Exception {
        mockMvc.perform(get("/api/patrons/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "View_Patrons")
    public void getPatronById_ShouldReturnNotFoundWhenPatronDoesNotExist() throws Exception {
        when(patronService.getPatronById(anyLong())).thenReturn(new PatronResponseDTO(null, null, null, null, null, null, null, null, false, Enums.StatusResponse.Failed, "Patron not found"));


        mockMvc.perform(get("/api/patrons/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("Patron not found"));
    }

    @Test
    @WithMockUser(roles = "Edit_Patrons")
    public void deletePatron_ShouldReturnNotFoundWhenPatronDoesNotExist() throws Exception {
        ResponseDTO responseDTO = new ResponseDTO(Enums.StatusResponse.Failed, "Patron not found");

        when(patronService.deletePatronById(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(delete("/api/patrons/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("Patron not found"));
    }

    @Test
    @WithMockUser(roles = "View_Patrons")
    public void getPatrons_ShouldReturnEmptyListWhenNoPatronsExist() throws Exception {
        AllPatronsResponseDTO allPatronsResponseDTO = new AllPatronsResponseDTO(
                Enums.StatusResponse.Success, "No patrons found", Collections.emptyList());

        when(patronService.getAllPatrons()).thenReturn(allPatronsResponseDTO);

        mockMvc.perform(get("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.message").value("No patrons found"))
                .andExpect(jsonPath("$.patrons").isEmpty());
    }

    @Test
    @WithMockUser(roles = "Edit_Patrons")
    public void deletePatron_ShouldReturnBadRequestOnFailure() throws Exception {
        ResponseDTO responseDTO = new ResponseDTO(Enums.StatusResponse.Failed, "Failed to delete patron");

        when(patronService.deletePatronById(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(delete("/api/patrons/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("Failed to delete patron"));
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
