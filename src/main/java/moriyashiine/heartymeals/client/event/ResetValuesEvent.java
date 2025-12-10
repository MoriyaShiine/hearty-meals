/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.client.event;

import moriyashiine.heartymeals.client.HeartyMealsClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class ResetValuesEvent implements ClientPlayConnectionEvents.Disconnect {
	@Override
	public void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
		HeartyMealsClient.forceDisableSprinting = false;
		HeartyMealsClient.naturalHealthRegeneration = true;
	}
}
