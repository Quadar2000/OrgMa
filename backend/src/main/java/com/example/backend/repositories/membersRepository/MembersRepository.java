package com.example.backend.repositories.membersRepository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entities.adress.Adress;
import com.example.backend.entities.members.Members;

public interface MembersRepository extends JpaRepository<Members, String>{
    boolean existsByAdress(Adress adress);
}
