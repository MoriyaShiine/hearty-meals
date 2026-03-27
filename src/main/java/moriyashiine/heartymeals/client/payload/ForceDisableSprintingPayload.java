/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.client.payload;

import moriyashiine.heartymeals.client.HeartyMealsClient;
import moriyashiine.heartymeals.common.HeartyMeals;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record ForceDisableSprintingPayload() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ForceDisableSprintingPayload> TYPE = new Type<>(HeartyMeals.id("force_disable_sprinting"));
	public static final StreamCodec<FriendlyByteBuf, ForceDisableSprintingPayload> CODEC = StreamCodec.unit(new ForceDisableSprintingPayload());

	@Override
	public Type<ForceDisableSprintingPayload> type() {
		return TYPE;
	}

	public static void send(ServerPlayer receiver) {
		ServerPlayNetworking.send(receiver, new ForceDisableSprintingPayload());
	}

	public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<ForceDisableSprintingPayload> {
		@Override
		public void receive(ForceDisableSprintingPayload payload, ClientPlayNetworking.Context context) {
			HeartyMealsClient.forceDisableSprinting = true;
		}
	}
}
