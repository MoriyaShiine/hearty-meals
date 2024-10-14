/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.common;

import eu.midnightdust.lib.config.MidnightConfig;
import moriyashiine.heartymeals.client.payload.ForceDisableSprintingPayload;
import moriyashiine.heartymeals.client.payload.SyncNaturalRegenPayload;
import moriyashiine.heartymeals.common.event.BedHealingEvent;
import moriyashiine.heartymeals.common.event.SyncValuesEvent;
import moriyashiine.heartymeals.common.init.ModStatusEffects;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import vectorwing.farmersdelight.common.registry.ModEffects;

public class HeartyMeals implements ModInitializer {
	public static final String MOD_ID = "heartymeals";

	public static boolean farmersDelightLoaded = false;

	public static RegistryEntry<StatusEffect> nourishmentEffect = null;

	@Override
	public void onInitialize() {
		MidnightConfig.init(MOD_ID, ModConfig.class);
		ModStatusEffects.init();
		initEvents();
		initPayloads();
		farmersDelightLoaded = FabricLoader.getInstance().isModLoaded("farmersdelight");
		if (farmersDelightLoaded) {
			nourishmentEffect = ModEffects.NOURISHMENT;
		}
	}

	public static Identifier id(String value) {
		return Identifier.of(MOD_ID, value);
	}

	private void initEvents() {
		ServerPlayConnectionEvents.JOIN.register(new SyncValuesEvent());
		EntitySleepEvents.STOP_SLEEPING.register(new BedHealingEvent());
	}

	private void initPayloads() {
		PayloadTypeRegistry.playS2C().register(ForceDisableSprintingPayload.ID, ForceDisableSprintingPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SyncNaturalRegenPayload.ID, SyncNaturalRegenPayload.CODEC);
	}
}