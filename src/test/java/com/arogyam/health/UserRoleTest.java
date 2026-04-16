package com.arogyam.health;

import com.arogyam.health.entity.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserRoleTest {

    @Test
    void requiredAuthorizationRolesExist() {
        assertNotNull(UserRole.valueOf("DOCTOR"));
        assertNotNull(UserRole.valueOf("ANALYST"));
        assertNotNull(UserRole.valueOf("HEALTH_WORKER"));
        assertNotNull(UserRole.valueOf("FIELD_AGENT"));
    }
}
