/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.client;

import com.google.common.collect.Ordering;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import moriyashiine.heartymeals.client.event.RenderFoodHealingEvent;
import moriyashiine.heartymeals.client.event.ResetValuesEvent;
import moriyashiine.heartymeals.client.payload.ForceDisableSprintingPayload;
import moriyashiine.heartymeals.client.payload.SyncNaturalRegenPayload;
import moriyashiine.heartymeals.common.init.ModStatusEffects;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HeartyMealsClient implements ClientModInitializer {
	public static boolean forceDisableSprinting = false, naturalRegen = true;

	public static boolean leaveMyBarsAloneLoaded = false;

	@Override
	public void onInitializeClient() {
		leaveMyBarsAloneLoaded = FabricLoader.getInstance().isModLoaded("leavemybarsalone");
		initEvents();
		initPayloads();
	}

	private void initEvents() {
		ClientPlayConnectionEvents.DISCONNECT.register(new ResetValuesEvent());
		ItemTooltipCallback.EVENT.register(new RenderFoodHealingEvent.Tooltip());
	}

	private void initPayloads() {
		ClientPlayNetworking.registerGlobalReceiver(ForceDisableSprintingPayload.ID, new ForceDisableSprintingPayload.Receiver());
		ClientPlayNetworking.registerGlobalReceiver(SyncNaturalRegenPayload.ID, new SyncNaturalRegenPayload.Receiver());
	}

	public static List<StatusEffectInstance> prioritizeCozy(Ordering<?> instance, Iterable<StatusEffectInstance> elements, Operation<List<StatusEffectInstance>> original) {
		List<StatusEffectInstance> effects = original.call(instance, elements);
		Set<StatusEffectInstance> removed = null;
		for (int i = effects.size() - 1; i >= 0; i--) {
			if (effects.get(i).getEffectType() == ModStatusEffects.COZY) {
				if (removed == null) {
					removed = new HashSet<>();
				}
				removed.add(effects.remove(i));
			}
		}
		if (removed != null) {
			for (StatusEffectInstance effectInstance : removed) {
				effects.add(0, effectInstance);
			}
		}
		return effects;
	}
}
