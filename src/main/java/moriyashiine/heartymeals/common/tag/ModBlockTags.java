/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.common.tag;

import moriyashiine.heartymeals.common.HeartyMeals;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModBlockTags {
	public static final TagKey<Block> COZY_SOURCES = TagKey.of(RegistryKeys.BLOCK, HeartyMeals.id("cozy_sources"));
}
