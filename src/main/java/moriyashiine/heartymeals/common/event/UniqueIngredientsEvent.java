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
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.recipe.*;
import net.minecraft.registry.Registries;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.MathHelper;
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
		for (RecipeEntry<CraftingRecipe> recipeEntry : server.getRecipeManager().getAllOfType(RecipeType.CRAFTING)) {
			try {
				if (recipeEntry.value().craft(null, server.getRegistryManager()).isOf(item)) {
					recipes++;
					uniqueIngredients += getUniqueIngredients(recipeEntry.value());
				}
			} catch (Exception ignored) {
			}
		}
		if (HeartyMeals.farmersDelightLoaded) {
			for (RecipeEntry<CookingPotRecipe> recipeEntry : server.getRecipeManager().getAllOfType(ModRecipeTypes.COOKING.get())) {
				try {
					if (recipeEntry.value().craft(null, server.getRegistryManager()).isOf(item)) {
						recipes++;
						uniqueIngredients += getUniqueIngredients(recipeEntry.value());
					}
				} catch (Exception ignored) {
				}
			}
		}
		return MathHelper.floor(uniqueIngredients / (float) recipes);
	}

	private static int getUniqueIngredients(Recipe<?> recipe) {
		Set<Ingredient> unique = new HashSet<>();
		for (Ingredient ingredient : recipe.getIngredientPlacement().getIngredients()) {
			if (ingredient.getMatchingItems().anyMatch(itemEntry -> itemEntry.isIn(ModItemTags.FOOD_INGREDIENTS))) {
				unique.add(ingredient);
			}
		}
		return unique.size();
	}

	private static void populate(MinecraftServer server) {
		UNIQUE_INGREDIENTS.clear();
		for (Item item : Registries.ITEM) {
			if (item.getComponents().contains(DataComponentTypes.FOOD) && !item.getDefaultStack().isIn(ModItemTags.IGNORES_INGREDIENT_BONUS)) {
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
		public void endDataPackReload(MinecraftServer server, LifecycledResourceManager resourceManager, boolean success) {
			populate(server);
			PlayerLookup.all(server).forEach(SyncUniqueIngredientsPayload::send);
		}
	}

	public static class Join implements ServerPlayConnectionEvents.Join {
		@Override
		public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
			SyncUniqueIngredientsPayload.send(handler.getPlayer());
		}
	}
}
