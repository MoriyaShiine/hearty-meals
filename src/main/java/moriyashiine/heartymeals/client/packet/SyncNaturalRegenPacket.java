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

public class SyncNaturalRegenPacket {
	public static final Identifier ID = HeartyMeals.id("sync_natural_regen");

	public static void send(ServerPlayerEntity player, boolean value) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBoolean(value);
		ServerPlayNetworking.send(player, ID, buf);
	}

	public static class Receiver implements ClientPlayNetworking.PlayChannelHandler {
		@Override
		public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
			boolean value = buf.readBoolean();
			client.execute(() -> HeartyMealsClient.naturalRegen = value);
		}
	}
}
