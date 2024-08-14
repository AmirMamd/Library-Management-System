package com.example.library.Service;

import com.example.library.Common.Enums;
import com.example.library.DTOs.Books.*;
import com.example.library.DTOs.ResponseDTO;
import com.example.library.Mapper.BookMapper;
import com.example.library.Models.Book;
import com.example.library.Repositories.IBooksRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BooksService {
    private final IBooksRepository IBooksRepository;


    public BooksService(IBooksRepository IBooksRepository) {
        this.IBooksRepository = IBooksRepository;
    }

    public AllBooksResponseDTO getAllBooks() {
        List<Book> books = IBooksRepository.findAll();

        if (!books.isEmpty()) {
            List<AllBooksDTO> allBooksDTOList = books.stream()
                    .map(book -> new AllBooksDTO(
                            book.getId(),
                            book.getTitle(),
                            book.getAuthor(),
                            book.getPublicationYear(),
                            book.getISBN()
                    ))
                    .collect(Collectors.toList());

            return new AllBooksResponseDTO(Enums.StatusResponse.Success, "Books retrieved successfully", allBooksDTOList);
        } else {
            return new AllBooksResponseDTO(Enums.StatusResponse.Success, "No books Found", null);
        }
    }

    public ResponseDTO addBook(BookDTO bookDTO) {
        Book book = BookMapper.mapToBook(bookDTO);
        try {
            IBooksRepository.save(book);
            return new ResponseDTO(Enums.StatusResponse.Success, "Book Added Successfully");
        } catch (Exception e) {
            log.error("Failed to Add Book: {}", e.getMessage(), e);
            return new ResponseDTO(Enums.StatusResponse.Failed, "Failed to Add Book");
        }
    }

    public BookResponseDTO getBookById(Long id) {
        Book book = IBooksRepository.findById(id).orElse(null);
        if (book != null) {
            return BookMapper.mapToBookDTO(book, Enums.StatusResponse.Success, "Book retrieved successfully");
        }
        else return new BookResponseDTO(null,null,null,null,null,Enums.StatusResponse.Failed, "Book not found");
    }

    @Transactional
    public ResponseDTO editBookById(Long id, BookDTO bookResponseDTO) {
        Book existingBook = IBooksRepository.findById(id).orElse(null);
        if (existingBook != null) {
            try {
                IBooksRepository.save(BookMapper.mapToExistingBook(bookResponseDTO,existingBook));
                return new ResponseDTO(Enums.StatusResponse.Success, "Book Edited Successfully");
            }
            catch (Exception e) {
                log.error("Failed to Edit Book: {}", e.getMessage(), e);
                return new ResponseDTO(Enums.StatusResponse.Failed, "Failed to Edit Book");
            }
        }
        else return new BookResponseDTO(null,null,null,null,null,Enums.StatusResponse.Failed, "Book not found");
    }

    public ResponseDTO deleteBookById(Long id) {
        try {
            IBooksRepository.deleteById(id);
            return new ResponseDTO(Enums.StatusResponse.Success, "Book deleted Successfully");
        }catch (Exception e) {
            log.error("Failed to Delete Book: {}", e.getMessage(), e);
            return new ResponseDTO(Enums.StatusResponse.Failed, "Failed to Delete Book");
        }
    }
}
