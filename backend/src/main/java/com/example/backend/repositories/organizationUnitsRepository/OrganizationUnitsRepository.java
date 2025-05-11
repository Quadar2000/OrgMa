package com.example.backend.repositories.organizationUnitsRepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entities.organizationUnits.OrganizationUnits;

public interface OrganizationUnitsRepository extends JpaRepository<OrganizationUnits, String>{

    Optional<OrganizationUnits> findByName(String name);
}

