package com.example.backend.services.membersService;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.entities.adress.Adress;
import com.example.backend.entities.members.Members;
import com.example.backend.entities.organizationUnits.OrganizationUnits;
import com.example.backend.entities.users.Users;
import com.example.backend.repositories.adressRepository.AdressRepository;
import com.example.backend.repositories.membersRepository.MembersRepository;
import com.example.backend.repositories.organizationUnitsRepository.OrganizationUnitsRepository;
import com.example.backend.repositories.usersRepository.UsersRepository;
import com.example.backend.requests.addMembersRequest.AddMemberRequest;

import jakarta.transaction.Transactional;

@Service
public class MembersService {

    private final MembersRepository membersRepository;

    private final UsersRepository usersRepository;

    private final OrganizationUnitsRepository organizationUnitsRepository;

    private final AdressRepository adressRepository;

    public MembersService(MembersRepository membersRepository, UsersRepository usersRepository,
     OrganizationUnitsRepository organizationUnitsRepository, AdressRepository adressRepository){
        this.membersRepository = membersRepository;
        this.usersRepository = usersRepository;
        this.organizationUnitsRepository = organizationUnitsRepository;
        this.adressRepository = adressRepository;
    }

    public List<Members> getMembers() {
        try{
            List<Members> members = membersRepository.findAll();
            return members;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void addMember(AddMemberRequest addMemberRequest) throws RuntimeException {

        if (usersRepository.findByEmail(addMemberRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Account with given email already exists");
        }


        Adress probe = new Adress();
        probe.setStreet(addMemberRequest.getStreet());
        probe.setBuildingNumber(addMemberRequest.getBuildingNumber());
        probe.setPremisesNumber(addMemberRequest.getPremisesNumber());
        probe.setCode(addMemberRequest.getCode());
        probe.setCity(addMemberRequest.getCity());
        probe.setRegion(addMemberRequest.getRegion());
        probe.setCountry(addMemberRequest.getCountry());

        ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnoreNullValues();

        Example<Adress> example = Example.of(probe, matcher);

        Adress adress = adressRepository.findOne(example)
            .orElseGet(() -> adressRepository.save(probe));

        OrganizationUnits unit = organizationUnitsRepository.findByName(addMemberRequest.getUnitName()).get();

        Members member = new Members();
        member.setName(addMemberRequest.getName());
        member.setSurname(addMemberRequest.getSurname());
        member.setBirthyear(addMemberRequest.getBirthyear());
        member.setJoinDate(addMemberRequest.getJoinDate());
        member.setPhoneNumber(addMemberRequest.getPhoneNumber());
        member.setJob(addMemberRequest.getJob());
        member.setFunction("member");
        member.setActive(true);
        member.setOrganizationUnit(unit);
        member.setAdress(adress);

        membersRepository.save(member);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();;

        String hashedPassword = passwordEncoder.encode(addMemberRequest.getPassword());
        
        Users user = new Users();
        user.setEmail(addMemberRequest.getEmail());
        user.setPassword(hashedPassword);
        user.setRole(addMemberRequest.getRole());
        user.setActive(true);
        user.setOrganizationUnit(unit);
        user.setMember(member);

        usersRepository.save(user);
        
    }

    @Transactional
    public void deleteMember(String memberId, String role) throws RuntimeException {
        Members member = membersRepository.findById(memberId)
        .orElseThrow(() -> new RuntimeException("Member not found"));

        Adress adress = member.getAdress();
        //relation is set on delete cascade on both sides, deleting user is equivalent to deleting member
        Users user = usersRepository.findByMemberIdFetchMember(memberId)
        .orElseThrow(() -> new RuntimeException("User not found"));


        Map<String, Integer> roleLevels = Map.of(
            "ROLE_OWNER", 1,
            "ROLE_ADMIN", 2,
            "ROLE_UNIT_ADMIN", 3,
            "ROLE_MEMBER", 4
        );

        int requesterLevel = roleLevels.getOrDefault(role, Integer.MAX_VALUE);
        int targetLevel = roleLevels.getOrDefault(user.getRole(), Integer.MAX_VALUE);

        if (requesterLevel >= targetLevel) {
            throw new RuntimeException("You do not have permission to delete this member");
        }

        usersRepository.delete(user);

        boolean isAdressUsed = membersRepository.existsByAdress(adress);

        if (!isAdressUsed) {
            adressRepository.delete(adress);
        }
    }
}
