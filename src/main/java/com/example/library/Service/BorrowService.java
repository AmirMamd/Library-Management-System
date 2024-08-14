package com.example.library.Service;


import com.example.library.Common.Enums;
import com.example.library.DTOs.BorrowAndReturn.BorrowAndReturnResponseDTO;
import com.example.library.DTOs.BorrowAndReturn.BorrowingRecordDTO;
import com.example.library.DTOs.BorrowAndReturn.ReturningRecordDTO;
import com.example.library.Mapper.BorrowingMapper;
import com.example.library.Models.Book;
import com.example.library.Models.BorrowingRecord;
import com.example.library.Models.Patron;
import com.example.library.Repositories.IBooksRepository;
import com.example.library.Repositories.IBorrowRepository;
import com.example.library.Repositories.IPatronRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BorrowService {

    private final IBooksRepository IBooksRepository;
    private final IPatronRepository IPatronRepository;
    private final IBorrowRepository IBorrowRepository;

    public BorrowService(IBooksRepository IBooksRepository, IPatronRepository IPatronRepository, IBorrowRepository IBorrowRepository) {
        this.IBooksRepository = IBooksRepository;
        this.IPatronRepository = IPatronRepository;
        this.IBorrowRepository = IBorrowRepository;
    }

    @Transactional
    public BorrowAndReturnResponseDTO borrowBook(Long bookId, Long patronId, BorrowingRecordDTO borrowingRecordDTO) {
        Book book = IBooksRepository.findById(bookId).orElse(null);
        if (book != null) {
            Patron existingPatron = IPatronRepository.findById(patronId).orElse(null);
            if (existingPatron != null) {
                try {
                    BorrowingRecord existingRecord = IBorrowRepository.findBorrowingRecordByBookAndPatronAndReturnDateIsNull(book, existingPatron);
                    if(existingRecord!=null) {
                        return new BorrowAndReturnResponseDTO(null, null, null, null, null,null, Enums.StatusResponse.Failed, "Book Is Already Borrowed");
                    }
                    BorrowingRecord record = BorrowingMapper.mapToBorrowingRecord(borrowingRecordDTO, book, existingPatron);
                    IBorrowRepository.save(record);
                    return new BorrowAndReturnResponseDTO(bookId,patronId,book.getTitle(),book.getAuthor(),book.getPublicationYear(),book.getISBN(),Enums.StatusResponse.Success, "Book Borrowed Successfully");


                } catch (Exception e) {
                    log.error("Failed To Borrow Book: {}", e.getMessage(), e);
                    return new BorrowAndReturnResponseDTO(null, null, null, null, null,null, Enums.StatusResponse.Failed, e.getMessage());
                }
            }
            else {
                return new BorrowAndReturnResponseDTO(null, null, null, null, null,null, Enums.StatusResponse.Failed, "Patron Not Found");
            }
        } else {
            return new BorrowAndReturnResponseDTO(null, null, null, null, null, null,Enums.StatusResponse.Failed, "Book  Not Found");
        }
    }

    @Transactional
    public BorrowAndReturnResponseDTO returnBook(Long bookId, Long patronId, ReturningRecordDTO returningRecordDTO) {
        Book book = IBooksRepository.findById(bookId).orElse(null);
        if (book != null) {
            Patron existingPatron = IPatronRepository.findById(patronId).orElse(null);
            if (existingPatron != null) {
                try {
                    BorrowingRecord record = IBorrowRepository.findBorrowingRecordByBookAndPatronAndReturnDateIsNull(book, existingPatron);
                    if (record != null ) {
                        record.setReturnDate(returningRecordDTO.getReturnDate());
                        IBorrowRepository.save(record);
                        return new BorrowAndReturnResponseDTO(bookId,patronId,book.getTitle(),book.getAuthor(),book.getPublicationYear(),book.getISBN(),Enums.StatusResponse.Success, "Book Returned Successfully");

                    }
                    return new BorrowAndReturnResponseDTO(null, null, null, null, null, null,Enums.StatusResponse.Failed, "Borrowing Record Doesn't Exist for this patron Or Book Is Already Returned");

                } catch (Exception e) {
                    log.error("Failed To Return Book: {}", e.getMessage(), e);
                    return new BorrowAndReturnResponseDTO(null, null, null, null, null,null, Enums.StatusResponse.Failed, e.getMessage());
                }
            }
            else {
                return new BorrowAndReturnResponseDTO(null, null, null, null, null,null, Enums.StatusResponse.Failed, "Patron Not Found");
            }
        } else {
            return new BorrowAndReturnResponseDTO(null, null, null, null, null, null,Enums.StatusResponse.Failed, "Book Not Found");
        }
    }
}
