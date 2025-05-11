package com.example.backend.entities.adress;

import com.example.backend.entities.members.Members;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Adress {
@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    private String street;

    @NotBlank
    @Column(nullable = false)
    private String buildingNumber;

    @Column
    private String premisesNumber;

    @NotBlank
    @Column(nullable = false)
    private String code;

    @NotBlank
    @Column(nullable = false)
    private String city;

    @NotBlank
    @Column(nullable = false)
    private String region;

    @NotBlank
    @Column(nullable = false)
    private String country;

    @OneToOne(mappedBy = "adress", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Members member;
}
