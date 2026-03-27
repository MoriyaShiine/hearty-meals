/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.client.event;

import moriyashiine.heartymeals.client.HeartyMealsClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public class ResetValuesEvent implements ClientPlayConnectionEvents.Disconnect {
	@Override
	public void onPlayDisconnect(ClientPacketListener listener, Minecraft client) {
		HeartyMealsClient.forceDisableSprinting = false;
		HeartyMealsClient.naturalHealthRegeneration = true;
	}
}
