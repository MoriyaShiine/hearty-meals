/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import moriyashiine.heartymeals.common.ModConfig;
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({HoneyBottleItem.class, MilkBucketItem.class, PotionItem.class})
public class DrinkableItemMixin {
	@ModifyReturnValue(method = "getMaxUseTime", at = @At("RETURN"))
	private int heartymeals$fasterFluidConsumption(int original) {
		if (ModConfig.fasterFluidConsumption) {
			return original / 2;
		}
		return original;
	}
}
