package com.example.library.DTOs.Books;

import com.example.library.Common.Enums;
import com.example.library.DTOs.ResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class BookResponseDTO extends ResponseDTO {

    private Long id;
    private String title;
    private String author;
    private Date publicationYear;
    private String ISBN;

    public BookResponseDTO(Long id, String title, String author, Date publicationYear, String ISBN, Enums.StatusResponse statusResponse, String message) {
        super(statusResponse, message);
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.ISBN = ISBN;
    }
}
