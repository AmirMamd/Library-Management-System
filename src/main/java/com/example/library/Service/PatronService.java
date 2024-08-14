package com.example.library.Service;

import com.example.library.Common.Enums;
import com.example.library.DTOs.Patron.AllPatronsResponseDTO;
import com.example.library.DTOs.Patron.PatronDTO;
import com.example.library.DTOs.Patron.PatronResponseDTO;
import com.example.library.DTOs.ResponseDTO;
import com.example.library.Mapper.PatronMapper;
import com.example.library.Models.Patron;
import com.example.library.Repositories.IPatronRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PatronService {

    private final IPatronRepository IPatronRepository;

    public PatronService(IPatronRepository IPatronRepository) {
        this.IPatronRepository = IPatronRepository;
    }


    public AllPatronsResponseDTO getAllPatrons() {
        List<Patron> patrons = IPatronRepository.findAll();
        if (!patrons.isEmpty()) {
            List<PatronDTO> allPatronsResponseDTO = patrons.stream()
                    .map(patron -> new PatronDTO(
                            patron.getId(),
                            patron.getFullName(),
                            patron.getAddress(),
                            patron.getEmail(),
                            patron.getUsername(),
                            null,
                            patron.getPhoneNumber(),
                            patron.getCountry(),
                            patron.isAdmin()
                    ))
                    .collect(Collectors.toList());

            return new AllPatronsResponseDTO(Enums.StatusResponse.Success, "Patrons retrieved successfully", allPatronsResponseDTO);
        } else {
            return new AllPatronsResponseDTO(Enums.StatusResponse.Success, "No Patrons found", null);
        }
    }

    public ResponseDTO deletePatron(Long id) {
        try {
            IPatronRepository.deleteById(id);
            return new ResponseDTO(Enums.StatusResponse.Success, "Patron Deleted Successfully");
        }catch (Exception e){
            log.error("Failed to delete Patron: {}", e.getMessage(), e);
            return new ResponseDTO(Enums.StatusResponse.Failed, "Failed To Delete Patron");
        }
    }

    public PatronResponseDTO getPatronById(Long id) {
        Patron patron = IPatronRepository.findById(id).orElse(null);
        if (patron != null) {
            return PatronMapper.mapToPatronDTO(patron, Enums.StatusResponse.Success, "Patron retrieved successfully");
        }
        else return new PatronResponseDTO(null,null,null,null,null, null,null, null,false, Enums.StatusResponse.Failed, "Patron not found");
    }

    @Transactional
    public ResponseDTO editPatronById(Long id, PatronDTO patronDTO) {
        Patron existingPatron = IPatronRepository.findById(id).orElse(null);
        if (existingPatron != null) {
            try {
                Patron updatedPatron = PatronMapper.mapToExistingPatron(patronDTO, existingPatron);
                IPatronRepository.save(updatedPatron);
                return new ResponseDTO(Enums.StatusResponse.Success, "Patron Edited Successfully");
            } catch (Exception e) {
                log.error("Failed to edit Patron: {}", e.getMessage(), e);
                return new ResponseDTO(Enums.StatusResponse.Failed, "Failed to Edit Patron");
            }
        } else {
            return new ResponseDTO(Enums.StatusResponse.Failed, "Patron not found");
        }
    }
}
