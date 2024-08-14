package com.example.library.DTOs.Patron;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PatronDTO {
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
