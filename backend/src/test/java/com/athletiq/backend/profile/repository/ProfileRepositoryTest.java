package com.athletiq.backend.profile.repository;

import com.athletiq.backend.profile.entity.Profile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileRepositoryTest {

    @Test
    void profileEntitySupportsUserOwnership() {
        Profile profile = new Profile();
        profile.setUserId(42L);
        assertEquals(42L, profile.getUserId());
    }
}