package com.example.backend.entities.users;

import org.springframework.data.annotation.PersistenceConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsersDTO {
    
    private String id;
    private String email;
    private Boolean active;

    @PersistenceConstructor
    public UsersDTO(String id, String email, Boolean active) {
        this.id = id;
        this.email = email;
        this.active = active;
    }
}
