package com.besson.crazyzombie.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.*;

public class ParalysisEffect extends StatusEffect {
    private float original_speed = 0.0F;

    public ParalysisEffect() {
        super(StatusEffectCategory.HARMFUL, 0X388E3C);
    }

    @Override
    public boolean isInstant() {
        return false;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        original_speed = entity.getMovementSpeed();
        entity.setMovementSpeed(0);
        super.applyUpdateEffect(entity, amplifier);
        return false;
    }

    @Override
    public void onEntityRemoval(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        entity.setMovementSpeed(original_speed);
        super.onEntityRemoval(entity, amplifier, reason);
    }
}
