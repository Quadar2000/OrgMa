package com.example.backend.services.membersService.strategies;

import java.util.List;

import com.example.backend.entities.adress.Adress;
import com.example.backend.entities.members.Members;
import com.example.backend.entities.organizationUnits.OrganizationUnits;
import com.example.backend.requests.addMembersRequest.AddMemberRequest;

public interface MembersServiceStrategy {

    boolean supports(String role);

    List<Members> getMembers(String requesterMemberId);

    Members addMember(AddMemberRequest addMemberRequest, Adress adress, OrganizationUnits unit);

    void deleteMember(String memberId, String requesterMemberId);

    Members getMemberById(String id);
}