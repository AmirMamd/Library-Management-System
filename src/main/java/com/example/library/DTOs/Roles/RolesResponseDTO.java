package com.example.library.DTOs.Roles;


import com.example.library.Common.Enums;
import com.example.library.DTOs.ResponseDTO;
import com.example.library.Models.Roles;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RolesResponseDTO extends ResponseDTO {
    private final List<Roles> data;
    public RolesResponseDTO(List<Roles> data,Enums.StatusResponse status, String Message) {
        super(status, Message);
        this.data = data;
    }
}
