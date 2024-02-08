/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.common.init;

import moriyashiine.heartymeals.common.HeartyMeals;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModTags {
	public static class BlockTags {
		public static final TagKey<Block> COZY_SOURCES = TagKey.of(RegistryKeys.BLOCK, HeartyMeals.id("cozy_sources"));
	}
}
