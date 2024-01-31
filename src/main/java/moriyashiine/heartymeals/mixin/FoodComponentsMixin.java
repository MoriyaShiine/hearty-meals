/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.mixin;

import moriyashiine.heartymeals.common.ModConfig;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoodComponents.class)
public class FoodComponentsMixin {
	@Inject(method = "createStew", at = @At("RETURN"))
	private static void heartymeals$fasterFluidConsumption(int hunger, CallbackInfoReturnable<FoodComponent.Builder> cir) {
		if (ModConfig.fasterFluidConsumption) {
			cir.getReturnValue().snack();
		}
	}
}
