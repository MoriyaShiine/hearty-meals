/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.common.event;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import moriyashiine.heartymeals.client.payload.SyncUniqueIngredientsPayload;
import moriyashiine.heartymeals.common.HeartyMeals;
import moriyashiine.heartymeals.common.tag.ModItemTags;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.HashSet;
import java.util.Set;

public class UniqueIngredientsEvent {
	public static final Object2IntMap<Item> UNIQUE_INGREDIENTS = new Object2IntOpenHashMap<>();

	public static int getUniqueIngredients(Item item) {
		return UNIQUE_INGREDIENTS.getOrDefault(item, 0);
	}

	private static int getUniqueIngredients(Item item, MinecraftServer server) {
		int recipes = 0, uniqueIngredients = 0;
		for (RecipeHolder<CraftingRecipe> recipeEntry : server.getRecipeManager().getAllOfType(RecipeType.CRAFTING)) {
			try {
				if (recipeEntry.value().assemble(null).is(item)) {
					recipes++;
					uniqueIngredients += getUniqueIngredients(recipeEntry.value());
				}
			} catch (Exception ignored) {
			}
		}
		if (HeartyMeals.farmersDelightLoaded) {
			for (RecipeHolder<CookingPotRecipe> recipeEntry : server.getRecipeManager().getAllOfType(ModRecipeTypes.COOKING.get())) {
				try {
					if (recipeEntry.value().assemble(null).is(item)) {
						recipes++;
						uniqueIngredients += getUniqueIngredients(recipeEntry.value());
					}
				} catch (Exception ignored) {
				}
			}
		}
		return Mth.floor(uniqueIngredients / (float) recipes);
	}

	private static int getUniqueIngredients(Recipe<?> recipe) {
		Set<Ingredient> unique = new HashSet<>();
		for (Ingredient ingredient : recipe.placementInfo().ingredients()) {
			if (ingredient.items().anyMatch(itemEntry -> itemEntry.is(ModItemTags.FOOD_INGREDIENTS))) {
				unique.add(ingredient);
			}
		}
		return unique.size();
	}

	private static void populate(MinecraftServer server) {
		UNIQUE_INGREDIENTS.clear();
		for (Item item : BuiltInRegistries.ITEM) {
			if (item.components().has(DataComponents.FOOD) && !item.getDefaultInstance().is(ModItemTags.IGNORES_INGREDIENT_BONUS)) {
				int uniqueIngredients = getUniqueIngredients(item, server);
				if (uniqueIngredients > 0) {
					UNIQUE_INGREDIENTS.put(item, uniqueIngredients);
				}
			}
		}
	}

	public static class Start implements ServerLifecycleEvents.ServerStarted {
		@Override
		public void onServerStarted(MinecraftServer server) {
			populate(server);
		}
	}

	public static class Reload implements ServerLifecycleEvents.EndDataPackReload {
		@Override
		public void endDataPackReload(MinecraftServer server, CloseableResourceManager resourceManager, boolean success) {
			populate(server);
			PlayerLookup.all(server).forEach(SyncUniqueIngredientsPayload::send);
		}
	}

	public static class Join implements ServerPlayConnectionEvents.Join {
		@Override
		public void onPlayReady(ServerGamePacketListenerImpl listener, PacketSender sender, MinecraftServer server) {
			SyncUniqueIngredientsPayload.send(listener.getPlayer());
		}
	}
}
