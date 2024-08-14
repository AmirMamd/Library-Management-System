package com.example.library.Mapper;

import com.example.library.DTOs.BorrowAndReturn.BorrowingRecordDTO;
import com.example.library.Models.Book;
import com.example.library.Models.BorrowingRecord;
import com.example.library.Models.Patron;

public class BorrowingMapper {

    public static BorrowingRecord mapToBorrowingRecord(BorrowingRecordDTO borrowingRecordDTO, Book book, Patron patron){
        return new BorrowingRecord(
            book,
            patron,
            borrowingRecordDTO.getBorrowDate(),
            null
        );
    }

}
