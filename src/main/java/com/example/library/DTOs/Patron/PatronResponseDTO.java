package com.example.library.DTOs.Patron;

import com.example.library.Common.Enums;
import com.example.library.DTOs.ResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PatronResponseDTO extends ResponseDTO {

    public PatronResponseDTO(Long id, String fullName,String address,  String email, String username, String password,String phoneNumber, String country, boolean admin,  Enums.StatusResponse statusResponse, String message) {
        super(statusResponse, message);
        this.id = id;
        this.fullName = fullName;
        this.address = address;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.admin = admin;
    }

    private Long id;
    private String fullName;
    private String address;
    private String email;
    private String username;
    private String password;
    private String phoneNumber;
    private String country;
    private boolean admin;


}
