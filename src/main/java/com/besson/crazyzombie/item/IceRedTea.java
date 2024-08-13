package com.besson.crazyzombie.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class IceRedTea extends PotionItem {
    public IceRedTea(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        user.getHungerManager()
                .eat(
                        ModFoodComponents.ICE_RED_TEA
                );
        return super.useOnEntity(stack, user, entity, hand);
    }
}
