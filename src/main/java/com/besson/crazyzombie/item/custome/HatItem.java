package com.besson.crazyzombie.item.custome;

import com.mojang.serialization.Codec;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class HatItem extends Item implements Equipment {
    private final Type type;

    public HatItem(Type type, Settings settings) {
        super(settings);
        this.type = type;
    }

    @Override
    public EquipmentSlot getSlotType() {
        return this.type.equipmentSlot;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return this.equipAndSwap(this, world, user, hand);
    }

    public static enum Type implements StringIdentifiable {
        HAT(EquipmentSlot.HEAD, 11, "hat");

        public static final Codec<ArmorItem.Type> CODEC = StringIdentifiable.createBasicCodec(ArmorItem.Type::values);
        private final EquipmentSlot equipmentSlot;
        private final String name;
        private final int baseMaxDamage;

        private Type(final EquipmentSlot equipmentSlot, final int baseMaxDamage, final String name) {
            this.equipmentSlot = equipmentSlot;
            this.name = name;
            this.baseMaxDamage = baseMaxDamage;
        }

        @Override
        public String asString() {
            return "";
        }
        }
}
