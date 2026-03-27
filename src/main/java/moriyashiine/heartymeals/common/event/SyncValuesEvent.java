/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.common.event;

import moriyashiine.heartymeals.client.payload.ForceDisableSprintingPayload;
import moriyashiine.heartymeals.client.payload.SyncNaturalHealthRegenerationPayload;
import moriyashiine.heartymeals.common.ModConfig;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.gamerules.GameRules;

public class SyncValuesEvent implements ServerPlayConnectionEvents.Join {
	@Override
	public void onPlayReady(ServerGamePacketListenerImpl listener, PacketSender sender, MinecraftServer server) {
		if (ModConfig.disableSprinting && !server.isSingleplayer()) {
			ForceDisableSprintingPayload.send(listener.getPlayer());
		}
		if (!listener.getPlayer().level().getGameRules().get(GameRules.NATURAL_HEALTH_REGENERATION)) {
			SyncNaturalHealthRegenerationPayload.send(listener.getPlayer(), false);
		}
	}
}
