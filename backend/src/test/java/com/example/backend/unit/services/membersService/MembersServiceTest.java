package com.example.backend.unit.services.membersService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.backend.entities.members.Members;
import com.example.backend.services.getMembersStrategyResolver.MembersServiceResolver;
import com.example.backend.services.membersService.MembersService;
import com.example.backend.services.membersService.strategies.MembersServiceStrategy;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MembersServiceTest {

    @Mock
    private MembersServiceResolver strategyResolver;

    @Mock
    private MembersServiceStrategy strategy;

    @Mock
    private UserDetails mockUserDetails;

    private MembersService membersService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        membersService = new MembersService(strategyResolver);

        var principal = User.withUsername("admin")
                .password("pass")
                .authorities("ROLE_ADMIN")
                .build();

        var auth = new UsernamePasswordAuthenticationToken(
                principal,
                principal.getPassword(),
                principal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getMembers_shouldDelegateToCorrectStrategy() {
        String requesterId = "req-id";
        List<Members> expected = List.of(new Members());

        when(strategyResolver.resolve("ROLE_ADMIN")).thenReturn(strategy);
        when(strategy.getMembers(requesterId)).thenReturn(expected);

        List<Members> result = membersService.getMembers(requesterId);

        assertEquals(expected, result);
        verify(strategy).getMembers(requesterId);
    }

    @Test
    void resolve_shouldThrowException_whenNoStrategySupportsRole() {
        MembersServiceStrategy mockStrategy = mock(MembersServiceStrategy.class);
        when(mockStrategy.supports("ROLE_UNKNOWN")).thenReturn(false);

        MembersServiceResolver resolver = new MembersServiceResolver(List.of(mockStrategy));

        assertThrows(IllegalArgumentException.class, () -> resolver.resolve("ROLE_UNKNOWN"));
    }
}
