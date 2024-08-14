package com.example.library.Mapper;

import com.example.library.Common.Enums;
import com.example.library.DTOs.Books.BookDTO;
import com.example.library.DTOs.Books.BookResponseDTO;
import com.example.library.Models.Book;

public class BookMapper {

    public static Book mapToBook(BookDTO bookDTO){
        return new Book(
                bookDTO.getAuthor(),
                bookDTO.getTitle(),
                bookDTO.getPublicationYear(),
                bookDTO.getISBN()
        );
    }

    public static Book mapToExistingBook(BookDTO bookResponseDTO, Book book){
        book.setAuthor(bookResponseDTO.getAuthor());
        book.setTitle(bookResponseDTO.getTitle());
        book.setPublicationYear(bookResponseDTO.getPublicationYear());
        book.setISBN(bookResponseDTO.getISBN());
        return book;
    }

    public static BookResponseDTO mapToBookDTO(Book book, Enums.StatusResponse statusResponse, String message){
        return new BookResponseDTO(
        book.getId(),
        book.getTitle(),
        book.getAuthor(),
        book.getPublicationYear(),
        book.getISBN(),
        statusResponse,
        message
    );
    }
}
