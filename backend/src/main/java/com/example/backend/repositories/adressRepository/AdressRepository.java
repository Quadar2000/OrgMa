package com.example.backend.repositories.adressRepository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entities.adress.Adress;

public interface AdressRepository extends JpaRepository<Adress, String>{
    
}
