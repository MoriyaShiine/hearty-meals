/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.client.payload;

import moriyashiine.heartymeals.client.HeartyMealsClient;
import moriyashiine.heartymeals.common.HeartyMeals;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public record SyncNaturalRegenPayload(boolean value) implements CustomPayload {
	public static final CustomPayload.Id<SyncNaturalRegenPayload> ID = new Id<>(HeartyMeals.id("sync_natural_regen"));
	public static final PacketCodec<PacketByteBuf, SyncNaturalRegenPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, SyncNaturalRegenPayload::value, SyncNaturalRegenPayload::new);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	public static void send(ServerPlayerEntity player, boolean value) {
		ServerPlayNetworking.send(player, new SyncNaturalRegenPayload(value));
	}

	public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<SyncNaturalRegenPayload> {
		@Override
		public void receive(SyncNaturalRegenPayload payload, ClientPlayNetworking.Context context) {
			HeartyMealsClient.naturalRegen = payload.value();
		}
	}
}
