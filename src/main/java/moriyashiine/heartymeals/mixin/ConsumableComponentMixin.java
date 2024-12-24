/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moriyashiine.heartymeals.common.ModConfig;
import net.minecraft.component.type.ConsumableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ConsumableComponent.class)
public class ConsumableComponentMixin {
	@ModifyExpressionValue(method = "consume", at = @At(value = "INVOKE", target = "Lnet/minecraft/component/type/ConsumableComponent;getConsumeTicks()I"))
	private int heartymeals$instantConsumption(int original) {
		if (ModConfig.instantConsumption) {
			return 0;
		}
		return original;
	}
}
