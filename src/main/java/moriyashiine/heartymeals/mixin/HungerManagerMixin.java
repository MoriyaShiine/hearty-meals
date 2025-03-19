/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moriyashiine.heartymeals.common.ModConfig;
import moriyashiine.heartymeals.common.component.entity.FoodHealingComponent;
import moriyashiine.heartymeals.common.init.ModEntityComponents;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
	@Unique
	private ServerPlayerEntity cachedPlayer = null;

	@Shadow
	public abstract void setFoodLevel(int foodLevel);

	@Shadow
	public abstract void setSaturationLevel(float saturationLevel);

	@Shadow
	private float exhaustion;

	@Inject(method = "update", at = @At("HEAD"))
	private void heartymeals$forceFullHunger(ServerPlayerEntity player, CallbackInfo ci) {
		cachedPlayer = player;
		setFoodLevel(20);
		setSaturationLevel(20);
		exhaustion = 0;
	}

	@ModifyExpressionValue(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;canFoodHeal()Z"))
	private boolean heartymeals$preventNaturalHealing(boolean value) {
		return false;
	}

	@Inject(method = "getFoodLevel", at = @At("HEAD"), cancellable = true)
	private void heartymeals$treatAsHealth(CallbackInfoReturnable<Integer> cir) {
		if (cachedPlayer != null) {
			cir.setReturnValue((int) (cachedPlayer.getHealth() / cachedPlayer.getMaxHealth() * 20));
		}
	}

	@ModifyVariable(method = "addInternal", at = @At("HEAD"), argsOnly = true)
	private float heartymeals$increasedSaturation(float value) {
		if (FoodHealingComponent.modifiedSaturation != -1) {
			float saturation = FoodHealingComponent.modifiedSaturation;
			FoodHealingComponent.modifiedSaturation = -1;
			return saturation;
		}
		return value;
	}

	@Inject(method = "addInternal", at = @At("HEAD"))
	private void heartymeals$treatAsHealth(int food, float saturation, CallbackInfo ci) {
		if (cachedPlayer != null) {
			if (ModConfig.instantRegeneration) {
				cachedPlayer.heal(food);
			} else {
				FoodHealingComponent foodHealingComponent = ModEntityComponents.FOOD_HEALING.get(cachedPlayer);
				foodHealingComponent.startHealing(food, saturation);
				foodHealingComponent.sync();
			}
		}
	}
}
