/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin;

import moriyashiine.heartymeals.common.ModConfig;
import moriyashiine.heartymeals.common.util.StewHolder;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.consume.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Item.Settings.class)
public class ItemSettingsMixin {
	@SuppressWarnings("unchecked")
	@ModifyVariable(method = "component", at = @At("HEAD"), argsOnly = true)
	private <T> T heartymeals$fasterFluidConsumption(T value, ComponentType<T> type) {
		if (ModConfig.fasterFluidConsumption && type == DataComponentTypes.CONSUMABLE) {
			ConsumableComponent component = (ConsumableComponent) value;
			if (component.useAction() == UseAction.DRINK) {
				return (T) new ConsumableComponent(component.consumeSeconds() / 2, component.useAction(), component.sound(), component.hasConsumeParticles(), component.onConsumeEffects());
			}
		}
		return value;
	}

	@ModifyVariable(method = "food(Lnet/minecraft/component/type/FoodComponent;Lnet/minecraft/component/type/ConsumableComponent;)Lnet/minecraft/item/Item$Settings;", at = @At("HEAD"), argsOnly = true)
	private ConsumableComponent heartymeals$fasterFluidConsumption(ConsumableComponent value, FoodComponent foodComponent) {
		if (ModConfig.fasterFluidConsumption && StewHolder.class.cast(foodComponent).heartymeals$isStew()) {
			return new ConsumableComponent(value.consumeSeconds() / 2, value.useAction(), value.sound(), value.hasConsumeParticles(), value.onConsumeEffects());
		}
		return value;
	}

	@ModifyVariable(method = "food(Lnet/minecraft/component/type/FoodComponent;Lnet/minecraft/component/type/ConsumableComponent;)Lnet/minecraft/item/Item$Settings;", at = @At("HEAD"), argsOnly = true)
	private FoodComponent heartymeals$increaseHoneySaturation(FoodComponent value) {
		if (ModConfig.increaseHoneySaturation && value == FoodComponents.HONEY_BOTTLE) {
			return new FoodComponent(value.nutrition(), value.saturation() * 8, value.canAlwaysEat());
		}
		return value;
	}
}
