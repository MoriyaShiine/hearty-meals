/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin;

import moriyashiine.heartymeals.common.HeartyMeals;
import moriyashiine.heartymeals.common.component.entity.FoodHealingComponent;
import moriyashiine.heartymeals.common.util.StewHolder;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodComponent.class)
public abstract class FoodComponentMixin implements StewHolder {
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

	@Inject(method = "onConsume", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;eat(Lnet/minecraft/component/type/FoodComponent;)V"))
	private void heartymeals$increasedSaturation(World world, LivingEntity user, ItemStack stack, ConsumableComponent consumable, CallbackInfo ci) {
		float modifiedSaturation = FoodHealingComponent.getModifiedSaturation(stack, saturation());
		if (modifiedSaturation != saturation()) {
			HeartyMeals.modifiedSaturation = modifiedSaturation;
		}
	}
}
