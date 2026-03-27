/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moriyashiine.heartymeals.common.ModConfig;
import moriyashiine.heartymeals.common.component.entity.FoodHealingComponent;
import moriyashiine.heartymeals.common.init.ModEntityComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FoodData.class, priority = 1001)
public abstract class FoodDataMixin {
	@Unique
	private ServerPlayer cachedPlayer = null;

	@Shadow
	public abstract boolean needsFood();

	@Shadow
	public abstract void setFoodLevel(int food);

	@Shadow
	public abstract void setSaturation(float saturation);

	@Shadow
	private float exhaustionLevel;

	@Inject(method = "tick", at = @At("HEAD"))
	private void heartymeals$forceFullHunger(ServerPlayer player, CallbackInfo ci) {
		cachedPlayer = player;
		if (needsFood()) {
			setFoodLevel(20);
			setSaturation(20);
			exhaustionLevel = 0;
		}
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isHurt()Z"))
	private boolean heartymeals$preventNaturalHealing(boolean original) {
		return false;
	}

	@Inject(method = "getFoodLevel", at = @At("HEAD"), cancellable = true)
	private void heartymeals$treatAsHealth(CallbackInfoReturnable<Integer> cir) {
		if (cachedPlayer != null) {
			cir.setReturnValue((int) (cachedPlayer.getHealth() / cachedPlayer.getMaxHealth() * 20));
		}
	}

	@Inject(method = "hasEnoughFood", at = @At("HEAD"), cancellable = true)
	private void heartymeals$allowSprinting(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}

	@ModifyVariable(method = "add", at = @At("HEAD"), argsOnly = true)
	private float heartymeals$increasedSaturation(float saturation) {
		if (FoodHealingComponent.modifiedSaturation != -1) {
			float modifiedSaturation = FoodHealingComponent.modifiedSaturation;
			FoodHealingComponent.modifiedSaturation = -1;
			return modifiedSaturation;
		}
		return saturation;
	}

	@Inject(method = "add", at = @At("HEAD"))
	private void heartymeals$treatAsHealth(int food, float saturation, CallbackInfo ci) {
		if (cachedPlayer != null) {
			food = Mth.floor(food * ModConfig.healthGainMultiplier);
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
