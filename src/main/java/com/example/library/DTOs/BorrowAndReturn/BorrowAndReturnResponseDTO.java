package com.example.library.DTOs.BorrowAndReturn;

import com.example.library.Common.Enums;
import com.example.library.DTOs.ResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BorrowAndReturnResponseDTO extends ResponseDTO {
    private Long bookId;
    private Long patronId;
    private String title;
    private String author;
    private Date publicationYear;
    private String ISBN;


    public BorrowAndReturnResponseDTO(Long bookId, Long patronId, String title, String author, Date publicationYear, String ISBN, Enums.StatusResponse statusResponse, String message) {
        super(statusResponse, message);
        this.bookId = bookId;
        this.patronId = patronId;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.ISBN = ISBN;
    }
}
