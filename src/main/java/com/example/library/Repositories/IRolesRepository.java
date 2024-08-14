package com.example.library.Repositories;

import com.example.library.Models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRolesRepository extends JpaRepository<Roles,Integer> {
    boolean existsByName(String name);


    List<Roles> findAllByNameIn(List<String> names);
}
