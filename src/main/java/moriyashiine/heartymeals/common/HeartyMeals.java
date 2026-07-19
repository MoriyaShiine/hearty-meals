/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.common;

import moriyashiine.heartymeals.client.payload.ForceDisableSprintingPayload;
import moriyashiine.heartymeals.client.payload.SyncNaturalHealthRegenerationPayload;
import moriyashiine.heartymeals.client.payload.SyncUniqueIngredientsPayload;
import moriyashiine.heartymeals.common.event.BedHealingEvent;
import moriyashiine.heartymeals.common.event.SyncValuesEvent;
import moriyashiine.heartymeals.common.event.UniqueIngredientsEvent;
import moriyashiine.heartymeals.common.init.HeartyMealsMobEffects;
import moriyashiine.strawberrylib.api.SLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

public class HeartyMeals implements ModInitializer {
	public static final String MOD_ID = "hearty_meals";

	public static boolean farmersDelightLoaded = false;

	@Override
	public void onInitialize() {
		SLib.init(MOD_ID);
		initRegistries();
		initPayloads();
		initEvents();
		farmersDelightLoaded = FabricLoader.getInstance().isModLoaded("farmersdelight");
	}

	public static Identifier id(String value) {
		return Identifier.fromNamespaceAndPath(MOD_ID, value);
	}

	private void initRegistries() {
		HeartyMealsMobEffects.init();
	}

	private void initPayloads() {
		PayloadTypeRegistry.clientboundPlay().register(ForceDisableSprintingPayload.TYPE, ForceDisableSprintingPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(SyncNaturalHealthRegenerationPayload.TYPE, SyncNaturalHealthRegenerationPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(SyncUniqueIngredientsPayload.TYPE, SyncUniqueIngredientsPayload.CODEC);
	}

	private void initEvents() {
		BedHealingEvent.init();
		SyncValuesEvent.init();
		UniqueIngredientsEvent.init();
	}
}