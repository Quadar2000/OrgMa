package com.example.backend.repositories.membersRepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.example.backend.entities.adress.Adress;
import com.example.backend.entities.members.Members;

public interface MembersRepository extends JpaRepository<Members, String>{
    boolean existsByAdress(Adress adress);

    @Query("SELECT m FROM Members m WHERE m.id <> :excludedId")
    List<Members> findAllExcept(@Param("excludedId") String excludedId);

    @Query("""
    SELECT m FROM Members m
    JOIN Users u ON u.member.id = m.id
    WHERE m.id <> :excludedMemberId
      AND u.role NOT IN :excludedRoles
      AND m.organizationUnit.id = :organizationId
    """)
    List<Members> findAllFiltered(
        @Param("excludedMemberId") String excludedMemberId,
        @Param("excludedRoles") List<String> excludedRoles,
        @Param("organizationId") String organizationId
    );

    @Query("""
    SELECT m FROM Members m
    JOIN Users u ON u.member.id = m.id
    WHERE m.id <> :excludedMemberId
      AND u.role NOT IN :excludedRoles
    """)
    List<Members> findAllFiltered(
        @Param("excludedMemberId") String excludedMemberId,
        @Param("excludedRoles") List<String> excludedRoles
    );
}
