package com.example.library.Controllers;

import com.example.library.Common.ApplicationRoles;
import com.example.library.Common.Enums;
import com.example.library.DTOs.Patron.AllPatronsResponseDTO;
import com.example.library.DTOs.Patron.PatronDTO;
import com.example.library.DTOs.Patron.PatronResponseDTO;
import com.example.library.DTOs.RegisterDTO;
import com.example.library.DTOs.ResponseDTO;
import com.example.library.DTOs.Roles.RolesDTO;
import com.example.library.Mapper.RolesMapper;
import com.example.library.Service.PatronService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/patrons")
@CrossOrigin(origins = "*")
@Validated
public class PatronController {
    private final PatronService patronService;

    public PatronController(PatronService patronService) {
        this.patronService = patronService;
    }

    private <T> ResponseEntity<T> createResponse(T response, Enums.StatusResponse status) {
        return status == Enums.StatusResponse.Success ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).View_Patrons)")
    public ResponseEntity<PatronResponseDTO> getPatronById(@PathVariable Long id)   {
        PatronResponseDTO response = patronService.getPatronById(id);
        return createResponse(response, response.getStatusResponse());

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).Edit_Patrons)")
    public ResponseEntity<ResponseDTO> editPatronById(@PathVariable Long id, @RequestBody @Validated PatronDTO patronDTO)   {
        ResponseDTO response = patronService.editPatronById(id, patronDTO );
        return createResponse(response, response.getStatusResponse());

    }

    @PostMapping("")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).Add_Patrons)")
    public ResponseEntity<ResponseDTO> addPatron( @RequestBody @Validated RegisterDTO registerDTO)   {
        ResponseDTO response = patronService.addPatron(registerDTO, PatronRoles());
        return createResponse(response, response.getStatusResponse());

    }
    private static List<RolesDTO> PatronRoles(){
        List<String> roleNames = Arrays.asList(ApplicationRoles.Borrow_Book, ApplicationRoles.Return_Book, ApplicationRoles.View_Books);
        return RolesMapper.mapRoleNamesToDTO(roleNames);
    }

    @GetMapping("")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).View_Patrons)")
    public ResponseEntity<AllPatronsResponseDTO> getPatrons()   {
        AllPatronsResponseDTO response = patronService.getAllPatrons();
        return createResponse(response, response.getStatusResponse());

    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(T(com.example.library.Common.ApplicationRoles).Delete_Patrons)")
    public ResponseEntity<ResponseDTO> deletePatronById(@PathVariable Long id)   {
        ResponseDTO response = patronService.deletePatronById(id);
        return createResponse(response, response.getStatusResponse());

    }
}
