package com.example.library.Controllers;

import com.example.library.Common.Enums;
import com.example.library.DTOs.Books.AllBooksResponseDTO;
import com.example.library.DTOs.Books.BookDTO;
import com.example.library.DTOs.Books.BookResponseDTO;
import com.example.library.DTOs.ResponseDTO;
import com.example.library.Service.BooksService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
@Validated
public class BooksController {

    private final BooksService booksService;

    public BooksController(BooksService booksService) {
        this.booksService = booksService;
    }

    @GetMapping("")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).View_Books)")
    public ResponseEntity<AllBooksResponseDTO> getAllBooks()   {
        AllBooksResponseDTO response = booksService.getAllBooks();
        if (response.getStatusResponse() == Enums.StatusResponse.Success) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).Add_Books)")
    public ResponseEntity<ResponseDTO> addBook(@RequestBody BookDTO bookDTO)   {
        ResponseDTO response = booksService.addBook(bookDTO);

        if (response.getStatusResponse() == Enums.StatusResponse.Success) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).View_Books)")
    public ResponseEntity<ResponseDTO> getBookById(@PathVariable Long id)   {
        BookResponseDTO response = booksService.getBookById(id);
        if (response.getStatusResponse() == Enums.StatusResponse.Success) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).Edit_Books)")
    public ResponseEntity<ResponseDTO> editBookById(@PathVariable Long id, @RequestBody BookDTO bookResponseDTO)   {
        ResponseDTO response = booksService.editBookById(id, bookResponseDTO);
        if (response.getStatusResponse() == Enums.StatusResponse.Success) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).Delete_Books)")
    public ResponseEntity<ResponseDTO> deleteBookById(@PathVariable Long id)   {
        ResponseDTO response = booksService.deleteBookById(id);
        if (response.getStatusResponse() == Enums.StatusResponse.Success) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
