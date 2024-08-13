package com.besson.crazyzombie.entity;

import com.besson.crazyzombie.CrazyZombie;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static EntityType<CrazyZombieEntity> CRAZY_ZOMBIE_ENTITY_ENTITY_TYPE;

    public static void registerModEntities() {
        // 注册实体类型
        CRAZY_ZOMBIE_ENTITY_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(CrazyZombie.NAME,"crazy_zombie"),
                EntityType.Builder.<CrazyZombieEntity>create(CrazyZombieEntity::new, SpawnGroup.MONSTER)
                        .dimensions(0.6f, 1.95f)
                        .build(null)
        );
    }

}
