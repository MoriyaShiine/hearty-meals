/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.data.provider;

import moriyashiine.heartymeals.common.tag.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class ModItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
	public ModItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		valueLookupBuilder(ModItemTags.FOOD_INGREDIENTS)
				.forceAddTag(ConventionalItemTags.FOODS)
				.forceAddTag(ConventionalItemTags.CROPS)
				.forceAddTag(ConventionalItemTags.EGGS)
				.forceAddTag(ConventionalItemTags.FLOWERS)
				.forceAddTag(ConventionalItemTags.MUSHROOMS)
				.forceAddTag(ConventionalItemTags.SEEDS)
				.add(Items.SUGAR);
		valueLookupBuilder(ModItemTags.IGNORES_INGREDIENT_BONUS)
				.forceAddTag(ConventionalItemTags.GOLDEN_FOODS);
		valueLookupBuilder(ModItemTags.INCREASED_SATURATION)
				.add(Items.PUMPKIN_PIE);

		valueLookupBuilder(TagKey.create(Registries.ITEM, fromNamespaceAndPath("enchancement", "cannot_automatically_consume")))
				.add(Items.GOLDEN_CARROT);
	}
}
