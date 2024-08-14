package com.example.library.DTOs;

import com.example.library.Common.Enums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ResponseDTO{
    private Enums.StatusResponse statusResponse;
    private String message;
}
