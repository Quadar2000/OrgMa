package com.example.backend.unit.strategies.adminMembersServiceStrategyTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.backend.entities.adress.Adress;
import com.example.backend.entities.members.Members;
import com.example.backend.entities.users.Users;
import com.example.backend.repositories.adressRepository.AdressRepository;
import com.example.backend.repositories.membersRepository.MembersRepository;
import com.example.backend.repositories.usersRepository.UsersRepository;
import com.example.backend.services.membersService.strategies.adminGetMembersStrategy.AdminMembersServiceStrategy;
import com.example.backend.services.roleHierarchyService.RoleHierarchyService;

public class AdminMembersServiceStrategyTest {

    @Mock
    private MembersRepository membersRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private AdressRepository adressRepository;

    @Mock
    private RoleHierarchyService roleHierarchy;

    @InjectMocks
    private AdminMembersServiceStrategy strategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldThrowIfAdminTriesToDeleteMemberWithHigherRole() {
        
        String adminId = "admin-id";
        String targetId = "target-id";

        Members targetMember = new Members();
        Adress adress = new Adress();
        targetMember.setId(targetId);
        targetMember.setAdress(adress);

        Users targetUser = new Users();
        targetUser.setRole("ROLE_OWNER");

        when(membersRepository.findById(targetId)).thenReturn(Optional.of(targetMember));
        when(usersRepository.findByMemberIdFetchMember(targetId)).thenReturn(Optional.of(targetUser));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            strategy.deleteMember(targetId, adminId)
        );

        assertEquals("You do not have permission to delete this member", ex.getMessage());

        verify(membersRepository, never()).delete(any());
        verify(adressRepository, never()).delete(any());
    }

    @Test
    void shouldThrowIfAdminTriesToDeleteThemself() {
        
        String adminId = "admin-id";
        String targetId = "admin-id";

        Members targetMember = new Members();
        Adress adress = new Adress();
        targetMember.setId(targetId);
        targetMember.setAdress(adress);

        Users targetUser = new Users();
        targetUser.setRole("ROLE_OWNER");

        when(membersRepository.findById(targetId)).thenReturn(Optional.of(targetMember));
        when(usersRepository.findByMemberIdFetchMember(targetId)).thenReturn(Optional.of(targetUser));
        when(roleHierarchy.hasHigherPrivilege("ROLE_ADMIN", "ROLE_OWNER")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            strategy.deleteMember(targetId, adminId)
        );

        assertEquals("Tried to delete requester.", ex.getMessage());

        verify(membersRepository, never()).delete(any());
        verify(adressRepository, never()).delete(any());
    }

    @Test
    void shouldDeleteSuccesfully_WhenAdressNotUsed() {
        
        String memberId = "m1";
        String requesterId = "admin1";
        Members member = new Members();
        Adress adress = new Adress();
        member.setId(memberId);
        member.setAdress(adress);

        Users user = new Users();
        user.setRole("ROLE_MEMBER");

        when(membersRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(usersRepository.findByMemberIdFetchMember(memberId)).thenReturn(Optional.of(user));
        when(roleHierarchy.hasHigherPrivilege("ROLE_ADMIN", "ROLE_MEMBER")).thenReturn(true);
        when(membersRepository.existsByAdress(adress)).thenReturn(false);

        strategy.deleteMember(memberId, requesterId);

        InOrder inOrder = inOrder(membersRepository, usersRepository, adressRepository);
        inOrder.verify(membersRepository).findById(memberId);
        inOrder.verify(usersRepository).findByMemberIdFetchMember(memberId);

        inOrder.verify(membersRepository).delete(member);
        inOrder.verify(membersRepository).existsByAdress(adress);
        inOrder.verify(adressRepository).delete(adress);

        verifyNoMoreInteractions(membersRepository, usersRepository, adressRepository);
    }
}
