/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.common;

import eu.midnightdust.lib.config.MidnightConfig;
import moriyashiine.heartymeals.common.event.BedHealingEvent;
import moriyashiine.heartymeals.common.event.SyncValuesEvent;
import moriyashiine.heartymeals.common.init.ModStatusEffects;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class HeartyMeals implements ModInitializer {
	public static final String MOD_ID = "heartymeals";

	public static boolean farmersDelightLoaded = FabricLoader.getInstance().isModLoaded("farmersdelight");
	public static boolean farmersDelightRefabricatedLoaded = FabricLoader.getInstance().getModContainer("farmersdelight").map(modContainer -> Objects.equals(modContainer.getMetadata().getVersion().getFriendlyString().split("-")[1].split("\\.")[0], "2")).orElse(false);

	@Override
	public void onInitialize() {
		MidnightConfig.init(MOD_ID, ModConfig.class);
		ModStatusEffects.init();
		initEvents();
	}

	public static Identifier id(String value) {
		return new Identifier(MOD_ID, value);
	}

	private void initEvents() {
		ServerPlayConnectionEvents.JOIN.register(new SyncValuesEvent());
		EntitySleepEvents.STOP_SLEEPING.register(new BedHealingEvent());
	}
}