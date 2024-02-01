/*
 * All Rights Reserved (c) MoriyaShiine
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
	@ModifyExpressionValue(method = "canSprint", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;getFoodLevel()I"))
	private int heartymeals$sprintFix(int value) {
		if (ModConfig.disableSprinting || HeartyMealsClient.forceDisableSprinting) {
			return 0;
		}
		return 20;
	}
}
