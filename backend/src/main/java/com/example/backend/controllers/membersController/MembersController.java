package com.example.backend.controllers.membersController;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entities.adress.Adress;
import com.example.backend.entities.members.Members;
import com.example.backend.entities.organizationUnits.OrganizationUnits;
import com.example.backend.entities.users.Users;
import com.example.backend.repositories.membersRepository.MembersRepository;
import com.example.backend.repositories.organizationUnitsRepository.OrganizationUnitsRepository;
import com.example.backend.repositories.usersRepository.UsersRepository;
import com.example.backend.requests.addMembersRequest.AddMemberRequest;
import com.example.backend.services.adressService.AdressService;
import com.example.backend.services.membersService.MembersService;
import com.example.backend.services.usersSevice.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/members")
public class MembersController {

    private final MembersService membersService;
    private final AdressService adressService;
    private final UsersService usersService;
    private final OrganizationUnitsRepository organizationUnitsRepository;
    protected final UsersRepository usersRepository;
    protected final MembersRepository membersRepository;

    public MembersController(MembersService membersService,AdressService adressService, UsersService usersService, 
    OrganizationUnitsRepository organizationUnitsRepository, UsersRepository usersRepository, MembersRepository membersRepository) {
        this.membersService = membersService;
        this.adressService = adressService;
        this.usersService = usersService;
        this.organizationUnitsRepository = organizationUnitsRepository;
        this.usersRepository = usersRepository;
        this.membersRepository = membersRepository;
    }

    @GetMapping("/get-members")
    public ResponseEntity<?> getMembers(@RequestParam String requesterMemberId){
        try{
            List<Members> members = membersService.getMembers(requesterMemberId);

            return ResponseEntity.ok(Map.of("members", members));      

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("add-member")
    @Transactional
    public ResponseEntity<?> addMember(HttpServletRequest request, @RequestBody @Valid AddMemberRequest addMemberRequest) {
        try{
            Adress adress = adressService.findOrAddAdress(addMemberRequest.getStreet(), addMemberRequest.getBuildingNumber(), addMemberRequest.getPremisesNumber(),
            addMemberRequest.getCode(), addMemberRequest.getCity(), addMemberRequest.getRegion(), addMemberRequest.getCountry());
            
            OrganizationUnits unit = organizationUnitsRepository.findByName(addMemberRequest.getUnitName())
                .orElseThrow(() -> new RuntimeException("Unit not found"));

            Members member = membersService.addMember(addMemberRequest, adress, unit);

            if (usersRepository.findByEmail(addMemberRequest.getEmail()).isPresent()) {
                throw new RuntimeException("Account with given email already exists");
            }

            Users user = usersService.addUser(addMemberRequest.getEmail(), addMemberRequest.getPassword(), addMemberRequest.getRole(), true, unit, member);

            member.setUser(user);
            membersRepository.save(member);

            return ResponseEntity.ok().body(Map.of("message", "Member added successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                     .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                     .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete-member")
    public ResponseEntity<?> deleteMember(@RequestParam String memberId, @RequestParam String requesterMemberId) {
        try{
            membersService.deleteMember(memberId,requesterMemberId);

            return ResponseEntity.ok().body(Map.of("message", "Member deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
        }   
    }
}
