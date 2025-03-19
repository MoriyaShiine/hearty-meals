/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.data.provider;

import moriyashiine.heartymeals.common.tag.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.util.Identifier.of;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
	public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
		super(output, completableFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
		getOrCreateTagBuilder(ModItemTags.FOOD_INGREDIENTS)
				.addOptionalTag(ConventionalItemTags.FOODS)
				.addOptionalTag(ConventionalItemTags.CROPS)
				.addOptionalTag(ConventionalItemTags.EGGS)
				.addOptionalTag(ConventionalItemTags.FLOWERS)
				.addOptionalTag(ConventionalItemTags.MUSHROOMS)
				.addOptionalTag(ConventionalItemTags.SEEDS)
				.add(Items.SUGAR);
		getOrCreateTagBuilder(ModItemTags.IGNORES_INGREDIENT_BONUS)
				.addOptionalTag(ConventionalItemTags.GOLDEN_FOODS);
		getOrCreateTagBuilder(ModItemTags.INCREASED_SATURATION)
				.add(Items.PUMPKIN_PIE);

		getOrCreateTagBuilder(TagKey.of(RegistryKeys.ITEM, of("enchancement", "cannot_automatically_consume")))
				.add(Items.GOLDEN_CARROT);
	}
}
