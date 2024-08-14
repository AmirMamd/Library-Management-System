package com.example.library.Repositories;

import com.example.library.Models.Patron;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IPatronRepository extends JpaRepository<Patron,Long>{
    Patron findByUsernameOrEmail(String username, String email);
}

