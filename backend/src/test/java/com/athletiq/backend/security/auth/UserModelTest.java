package com.athletiq.backend.security.auth;

import com.athletiq.backend.security.auth.entity.Role;
import com.athletiq.backend.security.auth.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

    @Test
    void allRolesExist() {
        assertEquals(4, Role.values().length);
        assertNotNull(Role.SUPER_ADMIN);
        assertNotNull(Role.ORGANIZER);
        assertNotNull(Role.STAFF);
        assertNotNull(Role.PLAYER);
    }

    @Test
    void userDefaultsAreCorrect() {
        User user = new User();

        assertFalse(user.isEmailVerified());
        assertTrue(user.isEnabled());
    }

    @Test
    void userStoresIdentityAndRole() {
        User user = new User();

        user.setEmail("organizer@athletiq.test");
        user.setFirstName("Test");
        user.setLastName("Organizer");
        user.setPasswordHash("encoded-password");
        user.setRole(Role.ORGANIZER);

        assertEquals("organizer@athletiq.test", user.getEmail());
        assertEquals("Test", user.getFirstName());
        assertEquals("Organizer", user.getLastName());
        assertEquals("encoded-password", user.getPasswordHash());
        assertEquals(Role.ORGANIZER, user.getRole());
    }
}
