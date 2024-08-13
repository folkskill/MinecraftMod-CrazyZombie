package com.besson.crazyzombie.world.gen;

import com.besson.crazyzombie.entity.ModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;

public class ModEntitySpawner {

    public static void addEntitySpawn() {
        BiomeModifications.addSpawn(BiomeSelectors.all().or(BiomeSelectors.foundInTheEnd()), SpawnGroup.MONSTER, ModEntities.CRAZY_ZOMBIE_ENTITY_ENTITY_TYPE, 65, 1, 2);
    }
}