package com.example.library.DTOs.Books;

import com.example.library.Common.Enums;
import com.example.library.DTOs.ResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter

public class AllBooksResponseDTO extends ResponseDTO {

    public AllBooksResponseDTO(Enums.StatusResponse statusResponse, String message, List<AllBooksDTO> bookList) {
        super(statusResponse, message);
        books = bookList;
    }

    List<AllBooksDTO> books;
}
