package com.example.backend.services.membersService.strategies;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.backend.entities.adress.Adress;
import com.example.backend.entities.members.Members;
import com.example.backend.entities.organizationUnits.OrganizationUnits;
import com.example.backend.entities.users.Users;
import com.example.backend.repositories.membersRepository.MembersRepository;
import com.example.backend.repositories.usersRepository.UsersRepository;
import com.example.backend.requests.addMembersRequest.AddMemberRequest;

public abstract class AbstractMembersServiceStrategy implements MembersServiceStrategy {

    protected final MembersRepository membersRepository;
    protected final UsersRepository usersRepository;
    

    public AbstractMembersServiceStrategy(MembersRepository membersRepository, UsersRepository usersRepository) {
        this.membersRepository = membersRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public Members getMemberById(String id) {
        return membersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    protected void saveMember(Members member, AddMemberRequest addMemberRequest, Adress adress, OrganizationUnits unit) {

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
    }

    protected void checkOrganizationUnit (String unitName) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String id = userDetails.getUsername();

        Users requesterUser = usersRepository.findById(id).orElseThrow(() -> new RuntimeException("Server error"));

        String requesterUnitName = requesterUser.getMember().getOrganizationUnit().getName();

        if (requesterUnitName != unitName) {
            throw new RuntimeException("You can only add member to your organization unit.");
        }
    }
}
