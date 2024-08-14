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

    private <T> ResponseEntity<T> createResponse(T response, Enums.StatusResponse status) {
        return status == Enums.StatusResponse.Success ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).View_Books)")
    public ResponseEntity<AllBooksResponseDTO> getAllBooks()   {
        AllBooksResponseDTO response = booksService.getAllBooks();
        return createResponse(response, response.getStatusResponse());

    }

    @PostMapping("")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).Add_Books)")
    public ResponseEntity<ResponseDTO> addBook(@RequestBody BookDTO bookDTO)   {
        ResponseDTO response = booksService.addBook(bookDTO);
        return createResponse(response, response.getStatusResponse());

    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).View_Books)")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        return booksService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body(new BookResponseDTO(null, null, null, null, null, Enums.StatusResponse.Failed, "Book not found")));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).Edit_Books)")
    public ResponseEntity<ResponseDTO> editBookById(@PathVariable Long id, @RequestBody BookDTO bookResponseDTO)   {
        ResponseDTO response = booksService.editBookById(id, bookResponseDTO);
        return createResponse(response, response.getStatusResponse());

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).Delete_Books)")
    public ResponseEntity<ResponseDTO> deleteBookById(@PathVariable Long id)   {
        ResponseDTO response = booksService.deleteBookById(id);
        return createResponse(response, response.getStatusResponse());

    }
}
