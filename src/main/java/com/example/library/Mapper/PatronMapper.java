package com.example.library.Mapper;

import com.example.library.Common.Enums;
import com.example.library.DTOs.Patron.PatronDTO;
import com.example.library.DTOs.Patron.PatronResponseDTO;
import com.example.library.DTOs.RegisterDTO;
import com.example.library.Models.Patron;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class PatronMapper {

    public static Patron mapToUsers(RegisterDTO registerDTO){
        return new Patron(
                registerDTO.getFullName(),
                registerDTO.getEmail(),
                registerDTO.getUsername(),
                new BCryptPasswordEncoder().encode(registerDTO.getPassword()),
                registerDTO.getPhoneNumber(),
                registerDTO.getCountry(),
                registerDTO.getAddress()
        );
    }

    public static PatronResponseDTO mapToPatronDTO(Patron patron,Enums.StatusResponse statusResponse, String message) {
            return new PatronResponseDTO(
                    patron.getId(),
                    patron.getFullName(),
                    patron.getAddress(),
                    patron.getEmail(),
                    patron.getUsername(),
                    patron.getPassword(),
                    patron.getPhoneNumber(),
                    patron.getCountry(),
                    patron.isAdmin(),
                    statusResponse,
                    message
            );
    }

    public static Patron mapToExistingPatron(PatronDTO patronDTO, Patron existingPatron) {
        existingPatron.setFullName(patronDTO.getFullName());
        existingPatron.setAddress(patronDTO.getAddress());
        existingPatron.setEmail(patronDTO.getEmail());
        existingPatron.setUsername(patronDTO.getUsername());
        existingPatron.setPhoneNumber(patronDTO.getPhoneNumber());
        existingPatron.setCountry(patronDTO.getCountry());
        existingPatron.setAdmin(patronDTO.isAdmin());
        return existingPatron;
    }
}
