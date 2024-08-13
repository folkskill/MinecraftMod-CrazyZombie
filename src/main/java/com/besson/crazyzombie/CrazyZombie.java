package com.besson.crazyzombie;

import com.besson.crazyzombie.effects.Effects;
import com.besson.crazyzombie.item.ModItemGroups;
import com.besson.crazyzombie.item.ModItems;
import com.besson.crazyzombie.world.gen.ModEntitySpawner;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.besson.crazyzombie.entity.ModEntities;

public class CrazyZombie implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String NAME = "crazyzombie";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Effects.registerEffects();
		ModItems.registerModItems();
		ModEntities.registerModEntities();
		ModItemGroups.registerModItemGroups();
		ModEntitySpawner.addEntitySpawn();
		LOGGER.info("Hello Fabric world!");
	}
}