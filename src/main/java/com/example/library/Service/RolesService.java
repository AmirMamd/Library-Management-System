package com.example.library.Service;

import com.example.library.Common.Enums;
import com.example.library.DTOs.ResponseDTO;
import com.example.library.DTOs.Roles.RolesDTO;
import com.example.library.Models.Roles;
import com.example.library.Repositories.IRolesRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolesService {
    private final IRolesRepository IRolesRepository;

    public RolesService(IRolesRepository IRolesRepository) {
        this.IRolesRepository = IRolesRepository;
    }


    public ResponseDTO saveRoles(List<RolesDTO> rolesDTO) {
        List<String> savedRoles = new ArrayList<>();
        if(rolesDTO.isEmpty()){
            return new ResponseDTO(Enums.StatusResponse.Failed, "No Roles Found");
        }else {
            for (RolesDTO r : rolesDTO) {

                if (!IRolesRepository.existsByName(r.getName())) {
                    Roles newRole = new Roles();
                    newRole.setName(r.getName());
                    IRolesRepository.save(newRole);
                    savedRoles.add(r.getName());
                }
            }
            if (savedRoles.size() == rolesDTO.size()) {
                return new ResponseDTO(Enums.StatusResponse.Success, "Roles Saved Successfully");
            } else {
                int notSavedCount = rolesDTO.size() - savedRoles.size();
                return new ResponseDTO(Enums.StatusResponse.Success, notSavedCount + "Roles are already saved");
            }
        }
    }

    public List<Roles> getExistingRoles(List<RolesDTO> roleDTOs) {

        List<String> roleNames = roleDTOs.stream()
                .map(RolesDTO::getName)
                .collect(Collectors.toList());

        return IRolesRepository.findAllByNameIn(roleNames);
    }




}
