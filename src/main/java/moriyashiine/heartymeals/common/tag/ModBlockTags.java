/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.common.tag;

import moriyashiine.heartymeals.common.HeartyMeals;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
	public static final TagKey<Block> COZY_SOURCES = TagKey.create(Registries.BLOCK, HeartyMeals.id("cozy_sources"));
}
