/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import moriyashiine.heartymeals.common.util.StewHolder;
import net.minecraft.world.food.FoodProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodProperties.Builder.class)
public class FoodPropertiesBuilderMixin implements StewHolder {
	@Unique
	private boolean isStew = false;

	@Override
	public boolean heartymeals$isStew() {
		return isStew;
	}

	@Override
	public void heartymeals$setStew(boolean stew) {
		isStew = stew;
	}

	@ModifyReturnValue(method = "build", at = @At("RETURN"))
	private FoodProperties heartymeals$stewHolder(FoodProperties original) {
		((StewHolder) (Object) original).heartymeals$setStew(heartymeals$isStew());
		return original;
	}
}
