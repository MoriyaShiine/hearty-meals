/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.datagen.provider;

import moriyashiine.heartymeals.common.tag.HeartyMealsItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.ItemIds;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class HeartyMealsItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
	public HeartyMealsItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		builder(HeartyMealsItemTags.FOOD_INGREDIENTS)
				.forceAddTag(ConventionalItemTags.FOODS)
				.forceAddTag(ConventionalItemTags.CROPS)
				.forceAddTag(ConventionalItemTags.EGGS)
				.forceAddTag(ConventionalItemTags.FLOWERS)
				.forceAddTag(ConventionalItemTags.MUSHROOMS)
				.forceAddTag(ConventionalItemTags.SEEDS)
				.add(ItemIds.SUGAR);
		builder(HeartyMealsItemTags.IGNORES_INGREDIENT_BONUS)
				.forceAddTag(ConventionalItemTags.GOLDEN_FOODS);
		builder(HeartyMealsItemTags.INCREASED_SATURATION)
				.add(ItemIds.PUMPKIN_PIE);

		builder(TagKey.create(Registries.ITEM, fromNamespaceAndPath("enchancement", "cannot_automatically_consume")))
				.add(ItemIds.GOLDEN_CARROT);
	}
}
