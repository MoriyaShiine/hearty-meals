/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.client.payload;

import moriyashiine.heartymeals.client.HeartyMealsClient;
import moriyashiine.heartymeals.common.HeartyMeals;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record SyncNaturalHealthRegenerationPayload(boolean value) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SyncNaturalHealthRegenerationPayload> TYPE = new Type<>(HeartyMeals.id("sync_natural_health_regeneration"));
	public static final StreamCodec<FriendlyByteBuf, SyncNaturalHealthRegenerationPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, SyncNaturalHealthRegenerationPayload::value,
			SyncNaturalHealthRegenerationPayload::new);

	@Override
	public Type<SyncNaturalHealthRegenerationPayload> type() {
		return TYPE;
	}

	public static void send(ServerPlayer receiver, boolean value) {
		ServerPlayNetworking.send(receiver, new SyncNaturalHealthRegenerationPayload(value));
	}

	public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<SyncNaturalHealthRegenerationPayload> {
		@Override
		public void receive(SyncNaturalHealthRegenerationPayload payload, ClientPlayNetworking.Context context) {
			HeartyMealsClient.naturalHealthRegeneration = payload.value();
		}
	}
}
