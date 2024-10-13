/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moriyashiine.heartymeals.common.ModConfig;
import moriyashiine.heartymeals.common.component.entity.FoodHealingComponent;
import moriyashiine.heartymeals.common.init.ModEntityComponents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	@Shadow
	public abstract boolean canFoodHeal();

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@ModifyExpressionValue(method = "canConsume", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;isNotFull()Z"))
	private boolean heartymeals$treatAsHealth(boolean value) {
		return canEat() && ModEntityComponents.FOOD_HEALING.get(this).canEat();
	}

	@Inject(method = "eatFood", at = @At("HEAD"), cancellable = true)
	private void heartymeals$startHealing(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
		if (!ModEntityComponents.FOOD_HEALING.get(this).canEat()) {
			cir.setReturnValue(ItemStack.EMPTY);
		}
	}

	@ModifyVariable(method = "eatFood", at = @At("HEAD"), argsOnly = true)
	private FoodComponent heartymeals$increasedSaturation(FoodComponent value, World world, ItemStack stack) {
		float modifiedSaturation = FoodHealingComponent.getModifiedSaturation(stack, value.saturation());
		if (value.saturation() != modifiedSaturation) {
			FoodComponent.Builder builder = new FoodComponent.Builder().nutrition(value.nutrition()).saturationModifier(FoodHealingComponent.getOriginalSaturation(modifiedSaturation, value.nutrition()));
			if (value.canAlwaysEat()) {
				builder.alwaysEdible();
			}
			if (value.eatSeconds() == 0.8) {
				builder.snack();
			}
			for (FoodComponent.StatusEffectEntry entry : value.effects()) {
				builder.statusEffect(entry.effect(), entry.probability());
			}
			value.usingConvertsTo().ifPresent(conversion -> builder.usingConvertsTo(conversion.getItem()));
			return builder.build();
		}
		return value;
	}

	@Unique
	private boolean canEat() {
		if (ModConfig.allowEatingWhenFull) {
			return true;
		}
		return canFoodHeal() && MathHelper.ceil(getHealth()) < getMaxHealth();
	}
}