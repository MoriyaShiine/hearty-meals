/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.common.event;

import moriyashiine.heartymeals.client.packet.ForceDisableSprintingPacket;
import moriyashiine.heartymeals.common.ModConfig;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class DisableSprintingEvent implements ServerPlayConnectionEvents.Join {
	@Override
	public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
		if (ModConfig.disableSprinting && !server.isSingleplayer()) {
			ForceDisableSprintingPacket.send(handler.getPlayer());
		}
	}
}
