/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.common.init;

import moriyashiine.heartymeals.common.HeartyMeals;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModTags {
	public static class BlockTags {
		public static final TagKey<Block> COZY_SOURCES = TagKey.of(RegistryKeys.BLOCK, HeartyMeals.id("cozy_sources"));
	}

	public static class ItemTags {
		public static final TagKey<Item> INCREASED_SATURATION = TagKey.of(RegistryKeys.ITEM, HeartyMeals.id("increased_saturation"));
	}
}
