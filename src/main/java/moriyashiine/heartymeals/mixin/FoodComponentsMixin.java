/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import moriyashiine.heartymeals.common.ModConfig;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodComponents.class)
public class FoodComponentsMixin {
	@ModifyReturnValue(method = "createStew", at = @At("RETURN"))
	private static FoodComponent.Builder heartymeals$fasterFluidConsumption(FoodComponent.Builder original) {
		if (ModConfig.fasterFluidConsumption) {
			return original.snack();
		}
		return original;
	}
}
