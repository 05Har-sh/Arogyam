package com.arogyam.health;

import com.arogyam.health.entity.UserEntity;
import com.arogyam.health.entity.VillageEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserVillageRelationTest {

    @Test
    void userStoresVillageEntityReference() {
        VillageEntity village = new VillageEntity();
        village.setId(42L);
        village.setName("Sample Village");
        UserEntity user = new UserEntity();
        user.setVillage(village);

        assertEquals(42L, user.getVillage().getId());
        assertEquals("Sample Village", user.getVillage().getName());
    }
}
