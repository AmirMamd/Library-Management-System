package com.example.library.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {

    private String fullName;

    private String email;

    private String username;

    private String password;

    private String phoneNumber;

    private String country;

    private String address;


}

