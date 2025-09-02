package com.example.backend.services.membersService.strategies.ownerGetMembersStrategy;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.entities.adress.Adress;
import com.example.backend.entities.members.Members;
import com.example.backend.entities.organizationUnits.OrganizationUnits;
import com.example.backend.repositories.adressRepository.AdressRepository;
import com.example.backend.repositories.membersRepository.MembersRepository;
import com.example.backend.repositories.usersRepository.UsersRepository;
import com.example.backend.requests.addMembersRequest.AddMemberRequest;
import com.example.backend.services.membersService.strategies.AbstractMembersServiceStrategy;

@Service
public class OwnerMembersServiceStrategy extends AbstractMembersServiceStrategy {

    private final AdressRepository adressRepository;

    public OwnerMembersServiceStrategy(
        MembersRepository membersRepository,
        UsersRepository usersRepository,
        AdressRepository adressRepository
    ) {
        super(membersRepository, usersRepository);
        this.adressRepository = adressRepository;
    }

    @Override
    public boolean supports(String role) {
        return "ROLE_OWNER".equals(role);
    }

    @Override
    public List<Members> getMembers(String requesterMemberId) {
        return membersRepository.findAllExcept(requesterMemberId);
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

        membersRepository.delete(member);

        boolean isAdressUsed = membersRepository.existsByAdress(member.getAdress());

        if (!isAdressUsed) {
            adressRepository.delete(member.getAdress());
        }
    }
}
