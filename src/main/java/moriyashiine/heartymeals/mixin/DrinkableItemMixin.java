/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.mixin;

import moriyashiine.heartymeals.common.ModConfig;
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({HoneyBottleItem.class, MilkBucketItem.class, PotionItem.class})
public class DrinkableItemMixin {
	@Inject(method = "getMaxUseTime", at = @At("RETURN"), cancellable = true)
	private void heartymeals$fasterFluidConsumption(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
		if (ModConfig.fasterFluidConsumption) {
			cir.setReturnValue(cir.getReturnValueI() / 2);
		}
	}
}
