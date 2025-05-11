package com.example.backend.entities.payments;

import java.time.LocalDateTime;

import com.example.backend.entities.organizationUnits.OrganizationUnits;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private double sum;

    @Lob
    @Column
    private String description;

    @Column
    private LocalDateTime PaymentDate;

    @NotBlank
    @Column(nullable = false)
    private LocalDateTime PaymentDeadline;

    @NotBlank
    @Column(nullable = false)
    private Boolean settled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", referencedColumnName = "id")
    private OrganizationUnits organizationUnit;
}
