/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.mixin;

import moriyashiine.heartymeals.common.ModConfig;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HoneyBottleItem.class)
public class HoneyBottleItemMixin {
	@Unique
	private static final FoodComponent BETTER_HONEY_BOTTLE = new FoodComponent.Builder().hunger(6).saturationModifier(0.8F).build();

	@ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
	private static Item.Settings heartymeals$increaseHoneySaturation(Item.Settings value) {
		if (ModConfig.increaseHoneySaturation) {
			return value.food(BETTER_HONEY_BOTTLE);
		}
		return value;
	}
}
