/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.client;

import com.google.common.collect.Ordering;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import moriyashiine.heartymeals.client.event.RenderFoodHealingEvent;
import moriyashiine.heartymeals.client.event.ResetValuesEvent;
import moriyashiine.heartymeals.client.payload.ForceDisableSprintingPayload;
import moriyashiine.heartymeals.client.payload.SyncNaturalHealthRegenerationPayload;
import moriyashiine.heartymeals.client.payload.SyncUniqueIngredientsPayload;
import moriyashiine.heartymeals.common.init.ModMobEffects;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HeartyMealsClient implements ClientModInitializer {
	public static boolean forceDisableSprinting = false, naturalHealthRegeneration = true;

	public static boolean leaveMyBarsAloneLoaded = false;

	@Override
	public void onInitializeClient() {
		leaveMyBarsAloneLoaded = FabricLoader.getInstance().isModLoaded("leavemybarsalone");
		initPayloads();
		initEvents();
	}

	private void initPayloads() {
		ClientPlayNetworking.registerGlobalReceiver(ForceDisableSprintingPayload.TYPE, new ForceDisableSprintingPayload.Receiver());
		ClientPlayNetworking.registerGlobalReceiver(SyncNaturalHealthRegenerationPayload.TYPE, new SyncNaturalHealthRegenerationPayload.Receiver());
		ClientPlayNetworking.registerGlobalReceiver(SyncUniqueIngredientsPayload.TYPE, new SyncUniqueIngredientsPayload.Receiver());
	}

	private void initEvents() {
		ClientTickEvents.END_LEVEL_TICK.register(new RenderFoodHealingEvent.Tick());
		ClientPlayConnectionEvents.DISCONNECT.register(new ResetValuesEvent());
		ItemTooltipCallback.EVENT.register(new RenderFoodHealingEvent.Tooltip());
	}

	public static List<MobEffectInstance> prioritizeCozy(Ordering<?> instance, Iterable<MobEffectInstance> elements, Operation<List<MobEffectInstance>> original) {
		List<MobEffectInstance> effects = original.call(instance, elements);
		Set<MobEffectInstance> removed = null;
		for (int i = effects.size() - 1; i >= 0; i--) {
			if (effects.get(i).getEffect() == ModMobEffects.COZY) {
				if (removed == null) {
					removed = new HashSet<>();
				}
				removed.add(effects.remove(i));
			}
		}
		if (removed != null) {
			for (MobEffectInstance effect : removed) {
				effects.addFirst(effect);
			}
		}
		return effects;
	}
}
