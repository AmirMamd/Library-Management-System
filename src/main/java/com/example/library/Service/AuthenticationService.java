package com.example.library.Service;


import com.example.library.Common.Enums;
import com.example.library.DTOs.Login.LoginDTO;
import com.example.library.DTOs.Login.LoginResponseDTO;
import com.example.library.DTOs.RegisterDTO;
import com.example.library.DTOs.ResponseDTO;
import com.example.library.DTOs.Roles.RolesDTO;
import com.example.library.Mapper.PatronMapper;
import com.example.library.Models.Patron;
import com.example.library.Repositories.IPatronRepository;
import com.example.library.Security.JWTGenerator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {
    private final IPatronRepository IPatronRepository;
    private final JWTGenerator jwtGenerator;
    private final AuthenticationManager authenticationManager;
    private final RolesService rolesService;


    public AuthenticationService(IPatronRepository IPatronRepository, JWTGenerator jwtGenerator, AuthenticationManager authenticationManager, RolesService rolesService) {
        this.IPatronRepository = IPatronRepository;
        this.jwtGenerator = jwtGenerator;
        this.authenticationManager = authenticationManager;
        this.rolesService = rolesService;
    }

    public LoginResponseDTO registerPatron(RegisterDTO registerDTO, List<RolesDTO> roles) {
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

    public LoginResponseDTO registerLibrarian(RegisterDTO registerDTO, List<RolesDTO> roles) {
        Patron user = PatronMapper.mapToUsers(registerDTO);
        ResponseDTO response = rolesService.saveRoles(roles);
        if(response.getStatusResponse() == Enums.StatusResponse.Success){
            user.setRoles(rolesService.getExistingRoles(roles));
        }
        user.setAdmin(true);
        try {
            IPatronRepository.save(user);
            return loginLibrarian(new LoginDTO(user.getUsername(), registerDTO.getPassword()),user);
        } catch (Exception e) {
            return new LoginResponseDTO(null, null ,Enums.StatusResponse.Failed, e.getMessage());
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

    public LoginResponseDTO loginLibrarian(LoginDTO loginDTO, Patron user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getIdentifier(),
                            loginDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtGenerator.generateToken(authentication);
            IPatronRepository.save(user);
            return new LoginResponseDTO( token, user.getId(),Enums.StatusResponse.Success, "Login Successful");
        }catch (Exception e){
            return new LoginResponseDTO(null, null ,Enums.StatusResponse.Failed, "Login Failed");
        }
    }


}
