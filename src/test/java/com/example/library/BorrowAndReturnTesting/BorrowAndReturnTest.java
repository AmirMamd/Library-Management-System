package com.example.library.BorrowAndReturnTesting;

import com.example.library.Common.ApplicationRoles;
import com.example.library.Common.Enums;
import com.example.library.Controllers.BorrowController;
import com.example.library.DTOs.BorrowAndReturn.BorrowAndReturnResponseDTO;
import com.example.library.DTOs.BorrowAndReturn.BorrowingRecordDTO;
import com.example.library.DTOs.BorrowAndReturn.ReturningRecordDTO;
import com.example.library.Security.CustomUserDetailsService;
import com.example.library.Security.JWTAuthenticationFilter;
import com.example.library.Security.JWTGenerator;
import com.example.library.Service.BorrowService;
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

import java.text.SimpleDateFormat;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(BorrowController.class)
public class BorrowAndReturnTest {


    @MockBean
    private BorrowService borrowService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private static JWTGenerator jwtGenerator;

    @MockBean
    private static CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "Borrow_Book")
    public void borrowBook_ShouldReturnSuccessResponse() throws Exception {
        BorrowingRecordDTO borrowingRecordDTO = new BorrowingRecordDTO();
        BorrowAndReturnResponseDTO borrowAndReturnResponseDTO = new BorrowAndReturnResponseDTO(1L,1L,"","",new SimpleDateFormat("yyyy-MM-dd").parse("2024-08-09"), "", Enums.StatusResponse.Success,"Book borrowed successfully");

        when(borrowService.borrowBook(anyLong(), anyLong(), any(BorrowingRecordDTO.class))).thenReturn(borrowAndReturnResponseDTO);

        mockMvc.perform(post("/api/borrow/{bookId}/patron/{patronId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(borrowingRecordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.message").value("Book borrowed successfully"));
    }

    @Test
    @WithMockUser(roles = "Borrow_Book")
    public void borrowBook_ShouldHandleEdgeCaseDates() throws Exception {
        BorrowingRecordDTO borrowingRecordDTO = new BorrowingRecordDTO();
        borrowingRecordDTO.setBorrowDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-02-29")); // Leap day

        BorrowAndReturnResponseDTO borrowAndReturnResponseDTO = new BorrowAndReturnResponseDTO(1L, 1L, "", "", new SimpleDateFormat("yyyy-MM-dd").parse("2024-02-29"), "", Enums.StatusResponse.Success, "Book borrowed successfully");

        when(borrowService.borrowBook(anyLong(), anyLong(), any(BorrowingRecordDTO.class))).thenReturn(borrowAndReturnResponseDTO);

        mockMvc.perform(post("/api/borrow/{bookId}/patron/{patronId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(borrowingRecordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.message").value("Book borrowed successfully"));
    }

    @Test
    public void borrowBook_ShouldReturnForbidden_WhenUserWithoutRoleTriesToBorrow() throws Exception {
        BorrowingRecordDTO borrowingRecordDTO = new BorrowingRecordDTO();

        mockMvc.perform(post("/api/borrow/{bookId}/patron/{patronId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(borrowingRecordDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "Borrow_Book")
    public void borrowBook_ShouldReturnBadRequestOnFailure() throws Exception {
        BorrowingRecordDTO borrowingRecordDTO = new BorrowingRecordDTO();
        BorrowAndReturnResponseDTO borrowAndReturnResponseDTO = new BorrowAndReturnResponseDTO(1L,1L,"","",new SimpleDateFormat("yyyy-MM-dd").parse("2024-08-09"), "", Enums.StatusResponse.Failed,"Failed to borrow book");

        when(borrowService.borrowBook(anyLong(), anyLong(), any(BorrowingRecordDTO.class))).thenReturn(borrowAndReturnResponseDTO);

        mockMvc.perform(post("/api/borrow/{bookId}/patron/{patronId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(borrowingRecordDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("Failed to borrow book"));
    }

    @Test
    @WithMockUser(roles = "Borrow_Book")
    public void borrowBook_ShouldReturnBadRequest_WhenBookAlreadyBorrowed() throws Exception {
        BorrowingRecordDTO borrowingRecordDTO = new BorrowingRecordDTO();
        BorrowAndReturnResponseDTO borrowAndReturnResponseDTO = new BorrowAndReturnResponseDTO(1L, 1L, "", "", new SimpleDateFormat("yyyy-MM-dd").parse("2024-08-09"), "", Enums.StatusResponse.Failed, "Book is already borrowed");

        when(borrowService.borrowBook(anyLong(), anyLong(), any(BorrowingRecordDTO.class))).thenReturn(borrowAndReturnResponseDTO);

        mockMvc.perform(post("/api/borrow/{bookId}/patron/{patronId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(borrowingRecordDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("Book is already borrowed"));
    }

    @Test
    @WithMockUser(roles = "Return_Book")
    public void returnBook_ShouldReturnSuccessResponse() throws Exception {
        ReturningRecordDTO returningRecordDTO = new ReturningRecordDTO();
        BorrowAndReturnResponseDTO returnResponseDTO = new BorrowAndReturnResponseDTO(1L,1L,"","",new SimpleDateFormat("yyyy-MM-dd").parse("2024-08-09"), "",Enums.StatusResponse.Success, "Book returned successfully");

        when(borrowService.returnBook(anyLong(), anyLong(), any(ReturningRecordDTO.class))).thenReturn(returnResponseDTO);

        mockMvc.perform(put("/api/return/{bookId}/patron/{patronId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(returningRecordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.message").value("Book returned successfully"));
    }

    @Test
    @WithMockUser(roles = "Return_Book")
    public void returnBook_ShouldReturnBadRequest_WhenBookNotBorrowed() throws Exception {
        ReturningRecordDTO returningRecordDTO = new ReturningRecordDTO();
        BorrowAndReturnResponseDTO returnResponseDTO = new BorrowAndReturnResponseDTO(1L, 1L, "", "", new SimpleDateFormat("yyyy-MM-dd").parse("2024-08-09"), "", Enums.StatusResponse.Failed, "Book was not borrowed");

        when(borrowService.returnBook(anyLong(), anyLong(), any(ReturningRecordDTO.class))).thenReturn(returnResponseDTO);

        mockMvc.perform(put("/api/return/{bookId}/patron/{patronId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(returningRecordDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("Book was not borrowed"));
    }

    @Test
    @WithMockUser(roles = "Return_Book")
    public void returnBook_ShouldReturnBadRequestOnFailure() throws Exception {
        ReturningRecordDTO returningRecordDTO = new ReturningRecordDTO();
        BorrowAndReturnResponseDTO returnResponseDTO = new BorrowAndReturnResponseDTO(1L,1L,"","",new SimpleDateFormat("yyyy-MM-dd").parse("2024-08-09"), "",Enums.StatusResponse.Failed, "Failed to return book");

        when(borrowService.returnBook(anyLong(), anyLong(), any(ReturningRecordDTO.class))).thenReturn(returnResponseDTO);

        mockMvc.perform(put("/api/return/{bookId}/patron/{patronId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(returningRecordDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("Failed to return book"));
    }

    @Test
    public void returnBook_ShouldReturnForbidden_WhenUserWithoutRoleTriesToReturn() throws Exception {
        ReturningRecordDTO returningRecordDTO = new ReturningRecordDTO();

        mockMvc.perform(put("/api/return/{bookId}/patron/{patronId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(returningRecordDTO)))
                .andExpect(status().isForbidden());
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
