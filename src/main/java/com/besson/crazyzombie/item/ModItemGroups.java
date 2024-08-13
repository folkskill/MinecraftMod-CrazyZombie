package com.besson.crazyzombie.item;

import com.besson.crazyzombie.CrazyZombie;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    //模组物品分类
    public static final RegistryKey<ItemGroup> CRAZY_ZOMBIE_GROUP = register("crazy_zombie_group");
    //注册物品栏
    private static RegistryKey<ItemGroup> register(String id) {
        return RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(CrazyZombie.NAME, id));
    }
    public static void registerModItemGroups() {
        Registry.register(Registries.ITEM_GROUP, CRAZY_ZOMBIE_GROUP,
               ItemGroup.create(null, -1)
                       .displayName(Text.translatable("itemGroup.crazy_zombie_group"))
                       .icon(() -> new ItemStack(ModItems.MIC))
                       .entries((displayContext, entries) -> {
                           Item[] item_list = {
                                   ModItems.GOLDEN_MIC,
                                   ModItems.MIC,
                                   ModItems.COPPER_SWORD,
                                   ModItems.RAPER_HAT
                           };
                           for (Item item : item_list) {
                               //加入物品
                               entries.add(item);
                           }
                       })
                       .build());
   }
}
