package com.example.backend.services.getMembersStrategyResolver;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.services.membersService.strategies.MembersServiceStrategy;

@Service
public class MembersServiceResolver {

    private final List<MembersServiceStrategy> strategies;

    public MembersServiceResolver(List<MembersServiceStrategy> strategies) {
        this.strategies = strategies;
    }

    public MembersServiceStrategy resolve(String role) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for role: " + role));
    }
}
