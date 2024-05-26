/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin;

import moriyashiine.heartymeals.common.ModConfig;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleContentsComponent.class)
public class BundleContentsComponentMixin {
	@Inject(method = "getOccupancy(Lnet/minecraft/item/ItemStack;)Lorg/apache/commons/lang3/math/Fraction;", at = @At("HEAD"), cancellable = true)
	private static void heartymeals$potionBundleStacking(ItemStack stack, CallbackInfoReturnable<Fraction> cir) {
		if (ModConfig.potionBundleStacking && stack.getItem() instanceof PotionItem) {
			cir.setReturnValue(Fraction.ONE_QUARTER);
		}
	}
}
