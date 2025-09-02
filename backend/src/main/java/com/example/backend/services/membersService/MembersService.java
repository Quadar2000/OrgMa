package com.example.backend.services.membersService;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.backend.entities.adress.Adress;
import com.example.backend.entities.members.Members;
import com.example.backend.entities.organizationUnits.OrganizationUnits;
import com.example.backend.requests.addMembersRequest.AddMemberRequest;
import com.example.backend.services.getMembersStrategyResolver.MembersServiceResolver;
import com.example.backend.services.membersService.strategies.MembersServiceStrategy;

@Service
public class MembersService {

    private final MembersServiceResolver strategyResolver;

    public MembersService(MembersServiceResolver strategyResolver){
        this.strategyResolver = strategyResolver;
    }

    public Members getMemberById(String id) {
        MembersServiceStrategy strategy = strategyResolver.resolve(getUserRole());
        return strategy.getMemberById(id);
    }

    public List<Members> getMembers(String requesterMemberId) {
        MembersServiceStrategy strategy = strategyResolver.resolve(getUserRole());
        return strategy.getMembers(requesterMemberId);
    }

    public Members addMember(AddMemberRequest addMemberRequest, Adress adress, OrganizationUnits unit) throws RuntimeException {
        MembersServiceStrategy strategy = strategyResolver.resolve(getUserRole());
        return strategy.addMember(addMemberRequest,adress, unit);
    }

    public void deleteMember(String memberId, String requesterMemberId) {
        try {
            MembersServiceStrategy strategy = strategyResolver.resolve(getUserRole());
            strategy.deleteMember(memberId, requesterMemberId);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }  
        catch (RuntimeException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private String getUserRole() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow();
    }
}
