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
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public record SyncUniqueIngredientsPayload(
		Object2IntMap<RegistryEntry<Item>> uniqueIngredients) implements CustomPayload {
	public static final CustomPayload.Id<SyncUniqueIngredientsPayload> ID = new Id<>(HeartyMeals.id("sync_unique_ingredients"));
	public static final PacketCodec<RegistryByteBuf, SyncUniqueIngredientsPayload> CODEC = PacketCodec.tuple(PacketCodecs.map(Object2IntOpenHashMap::new, PacketCodecs.registryEntry(RegistryKeys.ITEM), PacketCodecs.INTEGER), SyncUniqueIngredientsPayload::uniqueIngredients, SyncUniqueIngredientsPayload::new);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	public static void send(ServerPlayerEntity receiver) {
		Object2IntMap<RegistryEntry<Item>> entryMap = new Object2IntOpenHashMap<>();
		UniqueIngredientsEvent.UNIQUE_INGREDIENTS.forEach((item, amount) -> entryMap.put(Registries.ITEM.getEntry(item), amount));
		ServerPlayNetworking.send(receiver, new SyncUniqueIngredientsPayload(entryMap));
	}

	public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<SyncUniqueIngredientsPayload> {
		@Override
		public void receive(SyncUniqueIngredientsPayload payload, ClientPlayNetworking.Context context) {
			UniqueIngredientsEvent.UNIQUE_INGREDIENTS.clear();
			payload.uniqueIngredients().forEach((itemEntry, amount) -> UniqueIngredientsEvent.UNIQUE_INGREDIENTS.put(itemEntry.value(), amount));
		}
	}
}
