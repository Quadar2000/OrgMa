package com.example.backend.services.roleHierarchyService;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RoleHierarchyService {

    private final Map<String, Integer> roleLevels = Map.of(
        "ROLE_OWNER", 1,
        "ROLE_ADMIN", 2,
        "ROLE_UNIT_ADMIN", 3,
        "ROLE_MEMBER", 4
    );

    public int getLevel(String role) {
        return roleLevels.getOrDefault(role, Integer.MAX_VALUE);
    }

    public boolean hasHigherPrivilege(String roleA, String roleB) {
        return getLevel(roleA) < getLevel(roleB);
    }

    public boolean hasEqualOrHigherPrivilege(String roleA, String roleB) {
        return getLevel(roleA) <= getLevel(roleB);
    }

    public Map<String, Integer> getRoleLevels() {
        return roleLevels;
    }
}
