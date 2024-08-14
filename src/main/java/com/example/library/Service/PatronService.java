package com.example.library.Service;

import com.example.library.Common.Enums;
import com.example.library.DTOs.Login.LoginDTO;
import com.example.library.DTOs.Login.LoginResponseDTO;
import com.example.library.DTOs.Patron.AllPatronsResponseDTO;
import com.example.library.DTOs.Patron.PatronDTO;
import com.example.library.DTOs.Patron.PatronResponseDTO;
import com.example.library.DTOs.RegisterDTO;
import com.example.library.DTOs.ResponseDTO;
import com.example.library.DTOs.Roles.RolesDTO;
import com.example.library.Mapper.PatronMapper;
import com.example.library.Models.Patron;
import com.example.library.Repositories.IPatronRepository;
import com.example.library.Security.JWTGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PatronService {

    private final IPatronRepository IPatronRepository;
    private final RolesService rolesService;
    private final JWTGenerator jwtGenerator;
    private final AuthenticationManager authenticationManager;

    public PatronService(IPatronRepository IPatronRepository, JWTGenerator jwtGenerator, AuthenticationManager authenticationManager, RolesService rolesService) {
        this.IPatronRepository = IPatronRepository;
        this.jwtGenerator = jwtGenerator;
        this.authenticationManager = authenticationManager;
        this.rolesService = rolesService;
    }

    public AllPatronsResponseDTO getAllPatrons() {
        List<Patron> patrons = IPatronRepository.findAll();
        if (!patrons.isEmpty()) {
            List<PatronDTO> allPatronsResponseDTO = patrons.stream()
                    .filter(patron -> !patron.isAdmin())
                    .map(patron -> new PatronDTO(
                            patron.getId(),
                            patron.getFullName(),
                            patron.getAddress(),
                            patron.getEmail(),
                            patron.getUsername(),
                            null,
                            patron.getPhoneNumber(),
                            patron.getCountry(),
                            false
                    ))
                    .collect(Collectors.toList());

            return new AllPatronsResponseDTO(Enums.StatusResponse.Success, "Patrons retrieved successfully", allPatronsResponseDTO);
        } else {
            return new AllPatronsResponseDTO(Enums.StatusResponse.Success, "No Patrons found", null);
        }
    }

    public ResponseDTO deletePatronById(Long id) {
        try {
            Patron patron = IPatronRepository.findById(id).orElse(null);
            if (patron != null) {
                patron.getRoles().clear();
                IPatronRepository.save(patron);
            }
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

    public LoginResponseDTO addPatron(RegisterDTO registerDTO, List<RolesDTO> roles) {
        Patron user = PatronMapper.mapToUsers(registerDTO);
        ResponseDTO response = rolesService.saveRoles(roles);
        if(response.getStatusResponse() == Enums.StatusResponse.Success){
            user.setRoles(rolesService.getExistingRoles(roles));
        }
        try {
            IPatronRepository.save(user);

            return loginPatron(new LoginDTO(user.getUsername(), registerDTO.getPassword()));
        } catch (Exception e) {
            return new LoginResponseDTO(null, null , Enums.StatusResponse.Failed, e.getMessage());
        }
    }
    public LoginResponseDTO loginPatron(LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getIdentifier(),
                            loginDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtGenerator.generateToken(authentication);
            Patron user = IPatronRepository.findByUsernameOrEmail(loginDTO.getIdentifier(),loginDTO.getIdentifier());
            if(user != null && token != null)
            {
                return new LoginResponseDTO(token, user.getId(),Enums.StatusResponse.Success, "Login Successful");
            }
            return new LoginResponseDTO(null, null ,Enums.StatusResponse.Failed, "Wrong Username or Password");
        }
        catch (Exception e) {
            return new LoginResponseDTO(null, null ,Enums.StatusResponse.Failed, "Wrong Username or Password");
        }
    }
}
