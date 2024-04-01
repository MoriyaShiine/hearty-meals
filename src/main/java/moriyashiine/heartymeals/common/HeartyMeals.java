/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.common;

import com.nhoryzon.mc.farmersdelight.registry.EffectsRegistry;
import eu.midnightdust.lib.config.MidnightConfig;
import moriyashiine.heartymeals.common.event.BedHealingEvent;
import moriyashiine.heartymeals.common.event.SyncValuesEvent;
import moriyashiine.heartymeals.common.init.ModStatusEffects;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import vectorwing.farmersdelight.common.registry.ModEffects;

public class HeartyMeals implements ModInitializer {
	public static final String MOD_ID = "heartymeals";

	public static StatusEffect nourshingEffect = null;

	@Override
	public void onInitialize() {
		MidnightConfig.init(MOD_ID, ModConfig.class);
		ModContainer container = FabricLoader.getInstance().getModContainer("farmersdelight").orElse(null);
		if (container != null) {
			if (container.getMetadata().getVersion().getFriendlyString().split("-")[1].startsWith("2.")) {
				nourshingEffect = ModEffects.NOURISHMENT.get();
			} else {
				nourshingEffect = EffectsRegistry.NOURISHMENT.get();
			}
		}
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