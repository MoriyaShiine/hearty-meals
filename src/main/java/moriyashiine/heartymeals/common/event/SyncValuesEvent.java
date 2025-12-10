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
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.world.rule.GameRules;

public class SyncValuesEvent implements ServerPlayConnectionEvents.Join {
	@Override
	public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
		if (ModConfig.disableSprinting && !server.isSingleplayer()) {
			ForceDisableSprintingPayload.send(handler.getPlayer());
		}
		if (!handler.getPlayer().getEntityWorld().getGameRules().getValue(GameRules.NATURAL_HEALTH_REGENERATION)) {
			SyncNaturalHealthRegenerationPayload.send(handler.getPlayer(), false);
		}
	}
}
