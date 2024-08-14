package com.example.library.Mapper;

import com.example.library.DTOs.Roles.RolesDTO;
import com.example.library.Models.Roles;

import java.util.ArrayList;
import java.util.List;

public class RolesMapper {
    public static List<RolesDTO> mapRoleNamesToDTO(List<String> roleNames) {
        List<RolesDTO> roles = new ArrayList<>();

        for (String roleName : roleNames) {
            RolesDTO roleDTO = new RolesDTO();
            roleDTO.setId(0); // Set the ID as needed
            roleDTO.setName(roleName);
            roles.add(roleDTO);
        }

        return roles;
    }

    public static List<Roles> mapDTOToRoles(List<RolesDTO> roleNames) {
        List<Roles> roles = new ArrayList<>();

        for (RolesDTO roleName : roleNames) {
            Roles role = new Roles();
            role.setId(0); // Set the ID as needed
            role.setName(roleName.getName());
            roles.add(role);
        }
        return roles;
    }
}

