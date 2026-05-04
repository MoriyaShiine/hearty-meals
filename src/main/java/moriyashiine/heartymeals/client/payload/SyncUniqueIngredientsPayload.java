/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.client.payload;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import moriyashiine.heartymeals.common.HeartyMeals;
import moriyashiine.heartymeals.common.event.UniqueIngredientsEvent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

public record SyncUniqueIngredientsPayload(Object2IntMap<Holder<Item>> uniqueIngredients) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SyncUniqueIngredientsPayload> TYPE = new Type<>(HeartyMeals.id("sync_unique_ingredients"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncUniqueIngredientsPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.map(Object2IntOpenHashMap::new, ByteBufCodecs.holderRegistry(Registries.ITEM), ByteBufCodecs.INT), SyncUniqueIngredientsPayload::uniqueIngredients,
			SyncUniqueIngredientsPayload::new);

	@Override
	public Type<SyncUniqueIngredientsPayload> type() {
		return TYPE;
	}

	public static void send(ServerPlayer receiver) {
		Object2IntMap<Holder<Item>> entryMap = new Object2IntOpenHashMap<>();
		UniqueIngredientsEvent.UNIQUE_INGREDIENTS.forEach((item, amount) -> entryMap.put(BuiltInRegistries.ITEM.wrapAsHolder(item), amount));
		ServerPlayNetworking.send(receiver, new SyncUniqueIngredientsPayload(entryMap));
	}

	public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<SyncUniqueIngredientsPayload> {
		@Override
		public void receive(SyncUniqueIngredientsPayload payload, ClientPlayNetworking.Context context) {
			UniqueIngredientsEvent.UNIQUE_INGREDIENTS.clear();
			payload.uniqueIngredients().forEach((holder, amount) -> UniqueIngredientsEvent.UNIQUE_INGREDIENTS.put(holder.value(), amount));
		}
	}
}
