package com.example.library.Repositories;

import com.example.library.Models.Book;
import org.springframework.data.jpa.repository.JpaRepository;



public interface IBooksRepository extends JpaRepository<Book,Long> {

}
