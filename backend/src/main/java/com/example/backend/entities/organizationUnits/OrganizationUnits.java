package com.example.backend.entities.organizationUnits;

import java.util.Set;

import com.example.backend.entities.announcements.Announcements;
import com.example.backend.entities.applications.Applications;
import com.example.backend.entities.events.Events;
import com.example.backend.entities.payments.Payments;
import com.example.backend.entities.members.Members;
import com.example.backend.entities.users.Users;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class OrganizationUnits {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "organizationUnit", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Announcements> announcements;
    
    @OneToMany(mappedBy = "organizationUnit", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Members> members;

    @OneToMany(mappedBy = "organizationUnit", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Payments> incomes;

    @OneToMany(mappedBy = "organizationUnit", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Users> users;

    @OneToMany(mappedBy = "organizationUnit", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Events> events;

    @OneToMany(mappedBy = "organizationUnit", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Applications> applications;


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<Announcements> getAnnouncements() { return announcements; }
    public void setAnnouncements(Set<Announcements> announcements) { this.announcements= announcements; }

    public Set<Members> getMembers() { return members; }
    public void setMembers(Set<Members> members) { this.members = members; }

    public Set<Payments> getIncomes() { return incomes; }
    public void setIncomes(Set<Payments> incomes) { this.incomes = incomes; }

    public Set<Users> getUsers() { return users; }
    public void setUsers(Set<Users> users) { this.users = users; }

    public Set<Events> getEvents() { return events; }
    public void setEvents(Set<Events> events) { this.events = events; }

    public Set<Applications> getApplications() { return applications; }
    public void setApplications(Set<Applications> applications) { this.applications = applications; }
}
