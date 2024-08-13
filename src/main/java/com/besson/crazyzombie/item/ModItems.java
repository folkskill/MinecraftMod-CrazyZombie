package com.besson.crazyzombie.item;

import com.besson.crazyzombie.CrazyZombie;
import com.besson.crazyzombie.item.custome.HatItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public class ModItems {
    //定义模组里的物品
//    public static final Item ice_red_tea = registerItem("ice_red_tea", new Item(new Item.Settings()
//            .food(ModFoodComponents.ICE_RED_TEA)));
    public static final Item GOLDEN_MIC = registerItem("golden_mic", new Item(new Item.Settings()
            .maxCount(1)));
    public static final Item MIC = registerItem("mic", new Item(new Item.Settings()
            .maxCount(1)));
    public static final Item ICE_RED_TEA = registerItem((String)"ice_red_tea",
            (Item)(new IceRedTea(new Item.Settings()
                    .maxCount(1)
                    .component(DataComponentTypes.POTION_CONTENTS,
                               new PotionContentsComponent(Optional.of(Potions.STRENGTH), Optional.of(0xEDC234), List.of())
                                       .with(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 2000, 2))
                                       .with(new StatusEffectInstance(StatusEffects.SPEED, 2000, 1))
                                       .with(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1000, 2))
                                       .with(new StatusEffectInstance(StatusEffects.UNLUCK, 5000, 4))
                    )
            )
            )
    );
    public static final Item COPPER_SWORD = registerItem("copper_sword",
            new SwordItem(
                    ModToolMaterials.COPPER_INGOT,
                    new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.COPPER_INGOT,
                            1, -1F)
                    )
            )
    );
    public static final Item RAPER_HAT = registerItem(
            "raper_hat",
            new HatItem(HatItem.Type.HAT, new Item.Settings().maxDamage(55))
    );

    private static Item registerItem(String id, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(CrazyZombie.NAME, id), item);
    }

    //向物品栏加入内容
    private static void addItemToIG(FabricItemGroupEntries fabricItemGroupEntries) {
        Item[] item_list = {
                ICE_RED_TEA,
                GOLDEN_MIC,
                MIC
        };
        for (Item item : item_list) {
            fabricItemGroupEntries.add(item);
        }
    }

    public static void registerModItems() {
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.CRAZY_ZOMBIE_GROUP).register(ModItems::addItemToIG);
        CrazyZombie.LOGGER.info("try to register ModItems");
    }
}
