/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moriyashiine.heartymeals.client.HeartyMealsClient;
import moriyashiine.heartymeals.common.ModConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
	@ModifyExpressionValue(method = "canSprint", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;canSprintOrFly()Z"))
	private boolean heartymeals$disableSprinting(boolean original) {
		if (ModConfig.disableSprinting || HeartyMealsClient.forceDisableSprinting) {
			return false;
		}
		return original;
	}
}
