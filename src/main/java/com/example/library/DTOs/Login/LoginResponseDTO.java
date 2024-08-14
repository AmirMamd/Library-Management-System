package com.example.library.DTOs.Login;

import com.example.library.Common.Enums;
import com.example.library.DTOs.ResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponseDTO extends ResponseDTO {
    private String accessToken;
    private Long id;
    public LoginResponseDTO(String accessToken,Long id,Enums.StatusResponse status, String Message) {
        super(status, Message);
        this.accessToken = accessToken;
        this.id = id;
    }
}

