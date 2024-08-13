package com.besson.crazyzombie.effects;

import com.besson.crazyzombie.CrazyZombie;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class Effects {
    public static RegistryEntry<StatusEffect> PARALYSIS_EFFECT = null;

    public static void registerEffects() {
        PARALYSIS_EFFECT = register("paralysis_effect", new ParalysisEffect());
    }

    private static RegistryEntry<StatusEffect> register(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(CrazyZombie.NAME, id), statusEffect);
    }

}
