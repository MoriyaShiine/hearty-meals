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
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public record ForceDisableSprintingPayload() implements CustomPayload {
	public static final CustomPayload.Id<ForceDisableSprintingPayload> ID = new Id<>(HeartyMeals.id("force_disable_sprinting"));
	public static final PacketCodec<PacketByteBuf, ForceDisableSprintingPayload> CODEC = PacketCodec.unit(new ForceDisableSprintingPayload());

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	public static void send(ServerPlayerEntity receiver) {
		ServerPlayNetworking.send(receiver, new ForceDisableSprintingPayload());
	}

	public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<ForceDisableSprintingPayload> {
		@Override
		public void receive(ForceDisableSprintingPayload payload, ClientPlayNetworking.Context context) {
			HeartyMealsClient.forceDisableSprinting = true;
		}
	}
}
