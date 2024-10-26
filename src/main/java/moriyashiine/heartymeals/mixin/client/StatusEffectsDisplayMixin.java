/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin.client;

import com.google.common.collect.Ordering;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moriyashiine.heartymeals.client.HeartyMealsClient;
import moriyashiine.heartymeals.common.HeartyMeals;
import moriyashiine.heartymeals.common.init.ModStatusEffects;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(StatusEffectsDisplay.class)
public class StatusEffectsDisplayMixin {
	@Unique
	private static final Identifier COZY_BACKGROUND_LARGE = HeartyMeals.id("container/inventory/cozy_background_large");
	@Unique
	private static final Identifier COZY_BACKGROUND_SMALL = HeartyMeals.id("container/inventory/cozy_background_small");

	@Unique
	private static boolean isCozy = false;

	@WrapOperation(method = "drawStatusEffects(Lnet/minecraft/client/gui/DrawContext;II)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Ordering;sortedCopy(Ljava/lang/Iterable;)Ljava/util/List;"))
	private List<StatusEffectInstance> heartymeals$prioritizeCozy(Ordering<?> instance, Iterable<StatusEffectInstance> elements, Operation<List<StatusEffectInstance>> original) {
		return HeartyMealsClient.prioritizeCozy(instance, elements, original);
	}

	@ModifyVariable(method = "drawStatusEffectBackgrounds", at = @At("STORE"))
	private StatusEffectInstance heartymeals$trackCozy(StatusEffectInstance statusEffectInstance) {
		isCozy = statusEffectInstance.getEffectType() == ModStatusEffects.COZY;
		return statusEffectInstance;
	}

	@ModifyArg(method = "drawStatusEffectBackgrounds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V", ordinal = 0))
	private Identifier heartymeals$cozyBackgroundLarge(Identifier value) {
		if (isCozy) {
			return COZY_BACKGROUND_LARGE;
		}
		return value;
	}

	@ModifyArg(method = "drawStatusEffectBackgrounds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V", ordinal = 1))
	private Identifier heartymeals$cozyBackgroundSmall(Identifier value) {
		if (isCozy) {
			return COZY_BACKGROUND_SMALL;
		}
		return value;
	}
}
