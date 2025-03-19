/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.common;

import moriyashiine.heartymeals.client.payload.ForceDisableSprintingPayload;
import moriyashiine.heartymeals.client.payload.SyncNaturalRegenPayload;
import moriyashiine.heartymeals.client.payload.SyncUniqueIngredientsPayload;
import moriyashiine.heartymeals.common.event.BedHealingEvent;
import moriyashiine.heartymeals.common.event.SyncValuesEvent;
import moriyashiine.heartymeals.common.event.UniqueIngredientsEvent;
import moriyashiine.heartymeals.common.init.ModStatusEffects;
import moriyashiine.strawberrylib.api.SLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class HeartyMeals implements ModInitializer {
	public static final String MOD_ID = "heartymeals";

	public static boolean farmersDelightLoaded = false;

	@Override
	public void onInitialize() {
		SLib.init(MOD_ID);
		initRegistries();
		initEvents();
		initPayloads();
		farmersDelightLoaded = FabricLoader.getInstance().isModLoaded("farmersdelight");
	}

	public static Identifier id(String value) {
		return Identifier.of(MOD_ID, value);
	}

	private void initRegistries() {
		ModStatusEffects.init();
	}

	private void initEvents() {
		ServerLifecycleEvents.SERVER_STARTED.register(new UniqueIngredientsEvent.Start());
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(new UniqueIngredientsEvent.Reload());
		ServerPlayConnectionEvents.JOIN.register(new UniqueIngredientsEvent.Join());
		ServerPlayConnectionEvents.JOIN.register(new SyncValuesEvent());
		EntitySleepEvents.STOP_SLEEPING.register(new BedHealingEvent());
	}

	private void initPayloads() {
		PayloadTypeRegistry.playS2C().register(ForceDisableSprintingPayload.ID, ForceDisableSprintingPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SyncNaturalRegenPayload.ID, SyncNaturalRegenPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SyncUniqueIngredientsPayload.ID, SyncUniqueIngredientsPayload.CODEC);
	}
}