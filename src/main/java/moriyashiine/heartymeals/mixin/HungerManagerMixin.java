/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moriyashiine.heartymeals.common.component.entity.FoodHealingComponent;
import moriyashiine.heartymeals.common.init.ModEntityComponents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
	@Unique
	private PlayerEntity cachedPlayer = null;

	@Shadow
	public abstract void setFoodLevel(int foodLevel);

	@Shadow
	public abstract void setSaturationLevel(float saturationLevel);

	@Shadow
	public abstract void setExhaustion(float exhaustion);

	@Inject(method = "update", at = @At("HEAD"))
	private void heartymeals$forceFullHunger(PlayerEntity player, CallbackInfo ci) {
		cachedPlayer = player;
		setFoodLevel(20);
		setSaturationLevel(20);
		setExhaustion(0);
	}

	@ModifyExpressionValue(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;canFoodHeal()Z"))
	private boolean heartymeals$preventNaturalHealing(boolean value) {
		return false;
	}

	@Inject(method = "getFoodLevel", at = @At("HEAD"), cancellable = true)
	private void heartymeals$treatAsHealth(CallbackInfoReturnable<Integer> cir) {
		if (cachedPlayer != null) {
			cir.setReturnValue((int) (cachedPlayer.getHealth() / cachedPlayer.getMaxHealth() * 20));
		}
	}

	@Inject(method = "addInternal", at = @At("HEAD"))
	private void heartymeals$treatAsHealth(int food, float saturation, CallbackInfo ci) {
		if (cachedPlayer != null && !cachedPlayer.getWorld().isClient) {
			FoodHealingComponent foodHealingComponent = ModEntityComponents.FOOD_HEALING.get(cachedPlayer);
			foodHealingComponent.startHealing(food, saturation);
			foodHealingComponent.sync();
		}
	}

	@WrapOperation(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/component/type/FoodComponent;saturation()F"))
	private float heartymeals$increasedSaturation(FoodComponent instance, Operation<Float> original, ItemStack stack) {
		return FoodHealingComponent.getModifiedSaturation(stack, original.call(instance));
	}
}
