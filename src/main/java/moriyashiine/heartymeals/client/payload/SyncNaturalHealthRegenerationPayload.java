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

public record SyncNaturalHealthRegenerationPayload(boolean value) implements CustomPayload {
	public static final CustomPayload.Id<SyncNaturalHealthRegenerationPayload> ID = new Id<>(HeartyMeals.id("sync_natural_health_regeneration"));
	public static final PacketCodec<PacketByteBuf, SyncNaturalHealthRegenerationPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, SyncNaturalHealthRegenerationPayload::value, SyncNaturalHealthRegenerationPayload::new);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	public static void send(ServerPlayerEntity receiver, boolean value) {
		ServerPlayNetworking.send(receiver, new SyncNaturalHealthRegenerationPayload(value));
	}

	public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<SyncNaturalHealthRegenerationPayload> {
		@Override
		public void receive(SyncNaturalHealthRegenerationPayload payload, ClientPlayNetworking.Context context) {
			HeartyMealsClient.naturalHealthRegeneration = payload.value();
		}
	}
}
