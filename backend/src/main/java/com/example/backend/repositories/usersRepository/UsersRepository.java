package com.example.backend.repositories.usersRepository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.example.backend.entities.users.Users;
import com.example.backend.entities.users.UsersDTO;

public interface UsersRepository extends JpaRepository<Users, String> {
    
    Optional<Users> findByEmail(String email);

    Optional<Users> findById(String id);

    @Query("SELECT new com.example.backend.entities.users.UsersDTO(u.id, u.email, u.active) FROM Users u WHERE u.email = :email")
    Optional<UsersDTO> findIdUserDTOByEmail(String email);

    @Query("SELECT u FROM Users u JOIN FETCH u.member WHERE u.member.id = :memberId")
    Optional<Users> findByMemberIdFetchMember(@Param("memberId") String memberId);
    

    // @Query("SELECT new com.test.demo.entities.user.UserDTO(u.id, u.name, u.email) FROM User u WHERE u.id = :id")
    // Optional<UserDTO> findUserDTOById(String id);

    // @Query("SELECT new com.test.demo.entities.user.UserDTO(u.id, u.name, u.email) FROM User u WHERE u.role = 'USER'")
    // List<UserDTO> findUsersbyUserRole();
}
