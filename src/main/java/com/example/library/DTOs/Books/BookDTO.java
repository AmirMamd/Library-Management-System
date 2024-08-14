package com.example.library.DTOs.Books;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
public class BookDTO {
    private String title;
    private String author;
    private Date publicationYear;
    private String ISBN;
}
