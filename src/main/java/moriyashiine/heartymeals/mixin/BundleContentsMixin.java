/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.mixin;

import com.mojang.serialization.DataResult;
import moriyashiine.heartymeals.common.ModConfig;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleContents.class)
public class BundleContentsMixin {
	@Inject(method = "getWeight", at = @At("HEAD"), cancellable = true)
	private static void heartymeals$potionBundleStacking(ItemInstance item, CallbackInfoReturnable<DataResult<Fraction>> cir) {
		if (ModConfig.potionBundleStacking && item.typeHolder().value() instanceof PotionItem) {
			cir.setReturnValue(DataResult.success(Fraction.ONE_QUARTER));
		}
	}
}
