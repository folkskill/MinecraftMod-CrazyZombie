package com.besson.crazyzombie.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class ModFoodComponents {
    public static final FoodComponent ICE_RED_TEA = new FoodComponent.Builder().nutrition(8).saturationModifier(0.8f)
            .alwaysEdible()
            .statusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 2000), 0.8f)
            .build();
}