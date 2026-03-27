/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.common.tag;

import moriyashiine.heartymeals.common.HeartyMeals;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
	public static final TagKey<Item> FOOD_INGREDIENTS = TagKey.create(Registries.ITEM, HeartyMeals.id("food_ingredients"));
	public static final TagKey<Item> IGNORES_INGREDIENT_BONUS = TagKey.create(Registries.ITEM, HeartyMeals.id("ignores_ingredient_bonus"));
	public static final TagKey<Item> INCREASED_SATURATION = TagKey.create(Registries.ITEM, HeartyMeals.id("increased_saturation"));
}
