package com.example.library.DTOs.Patron;


import com.example.library.Common.Enums;
import com.example.library.DTOs.ResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter

public class AllPatronsResponseDTO extends ResponseDTO {

    public AllPatronsResponseDTO(Enums.StatusResponse statusResponse, String message,List<PatronDTO> patrons) {
        super(statusResponse, message);
        this.patrons = patrons;
    }

    List<PatronDTO> patrons;


}
