package com.example.backend.services.usersSevice;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.entities.members.Members;
import com.example.backend.entities.organizationUnits.OrganizationUnits;
import com.example.backend.entities.users.Users;
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

    public Users addUser(String email, String password, String role, Boolean isActive, OrganizationUnits unit, Members member){
        String hashedPassword = new BCryptPasswordEncoder().encode(password);

        Users user = new Users();
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setRole(role);
        user.setActive(isActive);
        user.setOrganizationUnit(unit);
        user.setMember(member);

        usersRepository.save(user);

        return user;
    }
}
