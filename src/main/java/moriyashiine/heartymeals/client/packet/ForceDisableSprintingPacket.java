/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.client.packet;

import io.netty.buffer.Unpooled;
import moriyashiine.heartymeals.client.HeartyMealsClient;
import moriyashiine.heartymeals.common.HeartyMeals;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ForceDisableSprintingPacket {
	public static final Identifier ID = HeartyMeals.id("force_disable_sprinting");

	public static void send(ServerPlayerEntity player) {
		ServerPlayNetworking.send(player, ID, new PacketByteBuf(Unpooled.buffer()));
	}

	public static class Receiver implements ClientPlayNetworking.PlayChannelHandler {
		@Override
		public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
			client.execute(() -> HeartyMealsClient.forceDisableSprinting = true);
		}
	}
}
