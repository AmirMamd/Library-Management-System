package com.example.library.Repositories;

import com.example.library.Models.Book;
import com.example.library.Models.BorrowingRecord;
import com.example.library.Models.Patron;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBorrowRepository extends JpaRepository<BorrowingRecord,Long> {
    BorrowingRecord findBorrowingRecordByBookAndPatronAndReturnDateIsNull(Book book, Patron patron);
}
