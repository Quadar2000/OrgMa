package com.example.backend.services.membersService.strategies.adminGetMembersStrategy;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.entities.adress.Adress;
import com.example.backend.entities.members.Members;
import com.example.backend.entities.organizationUnits.OrganizationUnits;
import com.example.backend.entities.users.Users;
import com.example.backend.repositories.adressRepository.AdressRepository;
import com.example.backend.repositories.membersRepository.MembersRepository;
import com.example.backend.repositories.usersRepository.UsersRepository;
import com.example.backend.requests.addMembersRequest.AddMemberRequest;
import com.example.backend.services.membersService.strategies.AbstractMembersServiceStrategy;
import com.example.backend.services.roleHierarchyService.RoleHierarchyService;

@Service
public class AdminMembersServiceStrategy extends AbstractMembersServiceStrategy{

    private final AdressRepository adressRepository;
    private final RoleHierarchyService roleHierarchy;

    public AdminMembersServiceStrategy(
        MembersRepository membersRepository,
        UsersRepository usersRepository,
        AdressRepository adressRepository,
        RoleHierarchyService roleHierarchy
    ) {
        super(membersRepository, usersRepository);
        this.adressRepository = adressRepository;
        this.roleHierarchy = roleHierarchy;
    }

    @Override
    public boolean supports(String role) {
        return "ROLE_ADMIN".equals(role);
    }

    @Override
    public List<Members> getMembers(String requesterMemberId) {
        return membersRepository.findAllFiltered(requesterMemberId, List.of("OWNER", "ADMIN"));
    }

    @Override
    public Members addMember(AddMemberRequest addMemberRequest, Adress adress, OrganizationUnits unit) {
        Members member = new Members();

        this.saveMember(member, addMemberRequest, adress, unit);

        return member;
    }

    @Override
    @Transactional
    public void deleteMember(String memberId, String requesterMemberId) {
        Members member = membersRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if(memberId == requesterMemberId) {
            throw new RuntimeException("Tried to delete requester.");
        }

        Users user = usersRepository.findByMemberIdFetchMember(memberId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!roleHierarchy.hasHigherPrivilege("ROLE_ADMIN", user.getRole())) {
            throw new RuntimeException("You do not have permission to delete this member");
        }

        membersRepository.delete(member);

        boolean isAdressUsed = membersRepository.existsByAdress(member.getAdress());

        if (!isAdressUsed) {
            adressRepository.delete(member.getAdress());
        }
    }
}
