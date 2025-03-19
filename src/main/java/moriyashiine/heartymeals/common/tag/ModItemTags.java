/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.common.tag;

import moriyashiine.heartymeals.common.HeartyMeals;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModItemTags {
	public static final TagKey<Item> FOOD_INGREDIENTS = TagKey.of(RegistryKeys.ITEM, HeartyMeals.id("food_ingredients"));
	public static final TagKey<Item> IGNORES_INGREDIENT_BONUS = TagKey.of(RegistryKeys.ITEM, HeartyMeals.id("ignores_ingredient_bonus"));
	public static final TagKey<Item> INCREASED_SATURATION = TagKey.of(RegistryKeys.ITEM, HeartyMeals.id("increased_saturation"));
}
