package com.example.library.Controllers;


import com.example.library.Common.Enums;
import com.example.library.DTOs.BorrowAndReturn.BorrowAndReturnResponseDTO;
import com.example.library.DTOs.BorrowAndReturn.BorrowingRecordDTO;
import com.example.library.DTOs.BorrowAndReturn.ReturningRecordDTO;
import com.example.library.Service.BorrowService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = "*")
@Validated
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {

        this.borrowService = borrowService;
    }

    private <T> ResponseEntity<T> createResponse(T response, Enums.StatusResponse status) {
        return status == Enums.StatusResponse.Success ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }


    @PostMapping("borrow/{bookId}/patron/{patronId}")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).Borrow_Book)")
    public ResponseEntity<BorrowAndReturnResponseDTO> borrowBook(@PathVariable Long bookId, @PathVariable Long patronId, @RequestBody BorrowingRecordDTO borrowingRecordDTO) {
        BorrowAndReturnResponseDTO response = borrowService.borrowBook(bookId, patronId, borrowingRecordDTO);
        return createResponse(response, response.getStatusResponse());

    }

    @PutMapping("return/{bookId}/patron/{patronId}")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).Return_Book)")
    public ResponseEntity<BorrowAndReturnResponseDTO> returnBook(@PathVariable Long bookId, @PathVariable Long patronId, @RequestBody ReturningRecordDTO returningRecordDTO) {
        BorrowAndReturnResponseDTO response = borrowService.returnBook(bookId, patronId, returningRecordDTO);
        return createResponse(response, response.getStatusResponse());

    }
}