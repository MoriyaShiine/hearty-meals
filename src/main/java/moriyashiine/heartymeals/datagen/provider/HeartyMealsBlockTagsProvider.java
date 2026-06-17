/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.datagen.provider;

import moriyashiine.heartymeals.common.tag.HeartyMealsBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class HeartyMealsBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {
	public HeartyMealsBlockTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		builder(HeartyMealsBlockTags.COZY_SOURCES)
				.forceAddTag(BlockTags.CAMPFIRES);
	}
}
