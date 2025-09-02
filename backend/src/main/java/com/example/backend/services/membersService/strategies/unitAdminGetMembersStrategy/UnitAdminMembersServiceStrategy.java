package com.example.backend.services.membersService.strategies.unitAdminGetMembersStrategy;

import java.util.List;

import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;

@Service
public class UnitAdminMembersServiceStrategy extends AbstractMembersServiceStrategy {

    private final AdressRepository adressRepository;
    private final RoleHierarchyService roleHierarchy;

    public UnitAdminMembersServiceStrategy(
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
        return "ROLE_UNIT_ADMIN".equals(role);
    }

    @Override
    public List<Members> getMembers(String requesterMemberId) {
        String organizationId = this.getMemberById(requesterMemberId)
                .getOrganizationUnit().getId();
        return membersRepository.findAllFiltered(requesterMemberId,
                List.of("OWNER", "ADMIN", "UNIT_ADMIN"), organizationId);
    }

    @Override
    public Members addMember(AddMemberRequest addMemberRequest, Adress adress, OrganizationUnits unit) {
        this.checkOrganizationUnit(addMemberRequest.getUnitName());

        Members member = new Members();

        this.saveMember(member, addMemberRequest, adress, unit);

        return member;
    }

    @Override
    @Transactional
    public void deleteMember(String memberId, String requesterMemberId) {
        Members member = membersRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Members requester = membersRepository.findById(requesterMemberId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));

        if(memberId == requesterMemberId) {
            throw new RuntimeException("Tried to delete requester.");
        }

        if (!requester.getOrganizationUnit().getId().equals(member.getOrganizationUnit().getId())) {
            throw new RuntimeException("No permission to delete member outside your unit");
        }

        Users user = usersRepository.findByMemberIdFetchMember(memberId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!roleHierarchy.hasHigherPrivilege("ROLE_UNIT_ADMIN", user.getRole())) {
            throw new RuntimeException("You do not have permission to delete this member");
        }

        membersRepository.delete(member);

        boolean isAdressUsed = membersRepository.existsByAdress(member.getAdress());

        if (!isAdressUsed) {
            adressRepository.delete(member.getAdress());
        }
    }
}
