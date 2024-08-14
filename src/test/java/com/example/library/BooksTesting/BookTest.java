package com.example.library.BooksTesting;

import com.example.library.Common.ApplicationRoles;
import com.example.library.Common.Enums;
import com.example.library.Controllers.BooksController;
import com.example.library.DTOs.Books.*;
import com.example.library.DTOs.ResponseDTO;
import com.example.library.Security.CustomUserDetailsService;
import com.example.library.Security.JWTAuthenticationFilter;
import com.example.library.Security.JWTGenerator;
import com.example.library.Service.BooksService;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(BooksController.class)
public class BookTest {

    @MockBean
    private BooksService booksService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private static JWTGenerator jwtGenerator;

    @MockBean
    private static CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "Add_Books")
    public void addBook_ShouldReturnSuccessResponse() throws Exception {
        BookDTO bookDTO = new BookDTO("jj","hh",new SimpleDateFormat("yyyy-MM-dd").parse("2024-08-08"),"pp");
        ResponseDTO responseDTO = new ResponseDTO(Enums.StatusResponse.Success,"Book added successfully");

        when(booksService.addBook(any(BookDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.message").value("Book added successfully"));
    }

    @Test
    @WithMockUser(roles = "Add_Books")
    public void addBook_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        BookDTO invalidBookDTO = new BookDTO("", "", null, ""); // Empty fields
        ResponseDTO responseDTO = new ResponseDTO(Enums.StatusResponse.Failed,"Failed to Add Book");
        when(booksService.addBook(any(BookDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidBookDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Failed to Add Book"));
    }
    @Test
    @WithMockUser(roles = "View_Books")
    public void addBook_ShouldReturnForbidden_WhenNoEditBooksRole() throws Exception {
        BookDTO bookDTO = new BookDTO("jj","hh",new SimpleDateFormat("yyyy-MM-dd").parse("2024-08-08"),"pp");
        ResponseDTO responseDTO = new ResponseDTO(Enums.StatusResponse.Failed,"Failed to Add Book");
        when(booksService.addBook(any(BookDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "View_Books")
    public void getBookById_ShouldReturnBookDetails_WhenBookExists() throws Exception {
        BookResponseDTO bookResponseDTO = new BookResponseDTO(
                1L,
                "Test Title",
                "Test Author",
                new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-05"),
                "1234567890123",
                Enums.StatusResponse.Success,
                "Book retrieved successfully"
        );

        when(booksService.getBookById(1L)).thenReturn(java.util.Optional.of(bookResponseDTO));

        mockMvc.perform(get("/api/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.message").value("Book retrieved successfully"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.author").value("Test Author"));
    }

    @Test
    @WithMockUser(roles = "View_Books")
    public void getAllBooks_ShouldReturnBooksList() throws Exception {
        AllBooksDTO book1 = new AllBooksDTO(1L, "Book Title 1", "Author 1", new SimpleDateFormat("yyyy-MM-dd").parse("2023-01-01"), "Publisher 1");
        AllBooksDTO book2 = new AllBooksDTO(2L, "Book Title 2", "Author 2", new SimpleDateFormat("yyyy-MM-dd").parse("2023-02-01"), "Publisher 2");
        List<AllBooksDTO> booksList = Arrays.asList(book1, book2);

        AllBooksResponseDTO allBooksResponseDTO = new AllBooksResponseDTO(
                Enums.StatusResponse.Success,
                "Books retrieved successfully",
                booksList
        );
        when(booksService.getAllBooks()).thenReturn(allBooksResponseDTO);

        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books").exists()); // Adjust based on your response structure
    }

    @Test
    @WithMockUser(roles = "View_Books")
    public void getAllBooks_ShouldReturnEmptyList_WhenNoBooksAvailable() throws Exception {
        AllBooksResponseDTO allBooksResponseDTO = new AllBooksResponseDTO(
                Enums.StatusResponse.Success,
                "No books available",
                Arrays.asList()
        );

        when(booksService.getAllBooks()).thenReturn(allBooksResponseDTO);

        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.message").value("No books available"))
                .andExpect(jsonPath("$.books").isEmpty());
    }

    @Test
    @WithMockUser(roles = "View_Books")
    public void getBookById_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
        when(booksService.getBookById(999L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/books/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    @WithMockUser(roles = "Edit_Books")
    public void updateBook_ShouldReturnSuccessResponse() throws Exception {
        BookDTO updateBookDTO = new BookDTO("newTitle","newAuthor",new SimpleDateFormat("yyyy-MM-dd").parse("2024-08-09"),"newPublisher");
        ResponseDTO responseDTO = new ResponseDTO(Enums.StatusResponse.Success, "Book updated successfully");

        when(booksService.editBookById(anyLong(), any(BookDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateBookDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.message").value("Book updated successfully"));
    }

    @Test
    @WithMockUser(roles = "Edit_Books")
    public void updateBookById_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        BookDTO invalidUpdateBookDTO = new BookDTO("", "", null, "");

        ResponseDTO responseDTO = new ResponseDTO(Enums.StatusResponse.Failed, "Failed to update book");

        when(booksService.editBookById(anyLong(), any(BookDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidUpdateBookDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("Failed to update book"));
    }

    @Test
    @WithMockUser(roles = "Delete_Books")
    public void deleteBook_ShouldReturnSuccessResponse() throws Exception {
        ResponseDTO responseDTO = new ResponseDTO(Enums.StatusResponse.Success, "Book deleted successfully");

        when(booksService.deleteBookById(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(delete("/api/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Success.toString()))
                .andExpect(jsonPath("$.message").value("Book deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "Delete_Books")
    public void deleteBookById_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
        ResponseDTO responseDTO = new ResponseDTO(Enums.StatusResponse.Failed, "Book not found");

        when(booksService.deleteBookById(999L)).thenReturn(responseDTO);

        mockMvc.perform(delete("/api/books/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusResponse").value(Enums.StatusResponse.Failed.toString()))
                .andExpect(jsonPath("$.message").value("Book not found"));
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
