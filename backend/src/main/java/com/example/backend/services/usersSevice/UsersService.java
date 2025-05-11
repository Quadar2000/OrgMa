package com.example.backend.services.usersSevice;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.entities.users.UsersDTO;
import com.example.backend.repositories.usersRepository.UsersRepository;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    public UsersService(UsersRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usersRepository = userRepository;
    }

    public UsersDTO getUserByEmail(String email) throws RuntimeException {
        return usersRepository.findIdUserDTOByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
