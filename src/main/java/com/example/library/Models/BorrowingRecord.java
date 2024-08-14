package com.example.library.Models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table
public class BorrowingRecord {

    public BorrowingRecord( Book book, Patron patron, Date borrowDate,@Nullable Date returnDate) {
        this.book = book;
        this.patron = patron;
        this.borrowDate=borrowDate;
        this.returnDate=returnDate;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "patron_id", nullable = false)
    private Patron patron;

    @Column(name = "borrow_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date borrowDate;

    @Column(name = "return_date")
    @Temporal(TemporalType.DATE)
    @Nullable
    private Date returnDate;


}
