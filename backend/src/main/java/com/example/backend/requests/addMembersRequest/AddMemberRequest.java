package com.example.backend.requests.addMembersRequest;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMemberRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    @NotNull(message = "Birthyear is required")
    private LocalDate birthyear;

    @NotNull(message = "Join Date is required")
    private LocalDate joinDate;

    private String street;

    @NotBlank(message = "Building Number is required")
    private String buildingNumber;

    private String premisesNumber;

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Coty is required")
    private String city;

    @NotBlank(message = "Region is required")
    private String region;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String job;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$", message = "Password must contain small and big letters, one special sign, and numbers")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;

    @NotBlank(message = "unit name is required")
    private String unitName;
}
