/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.mixin;

import moriyashiine.heartymeals.common.component.entity.FoodHealingComponent;
import moriyashiine.heartymeals.common.util.StewHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodProperties.class)
public abstract class FoodPropertiesMixin implements StewHolder {
	@Unique
	private boolean isStew = false;

	@Shadow
	public abstract float saturation();

	@Override
	public boolean heartymeals$isStew() {
		return isStew;
	}

	@Override
	public void heartymeals$setStew(boolean stew) {
		isStew = stew;
	}

	@Inject(method = "onConsume", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"))
	private void heartymeals$increasedSaturation(Level level, LivingEntity user, ItemStack stack, Consumable consumable, CallbackInfo ci) {
		float modifiedSaturation = FoodHealingComponent.getModifiedSaturation(stack, saturation());
		if (modifiedSaturation != saturation()) {
			FoodHealingComponent.modifiedSaturation = modifiedSaturation;
		}
	}
}
