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
	private static final Identifier COZY_BACKGROUND = HeartyMeals.id("container/inventory/cozy_background");

	@Unique
	private static boolean isCozy = false;

	@WrapOperation(method = "drawStatusEffects", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Ordering;sortedCopy(Ljava/lang/Iterable;)Ljava/util/List;"))
	private List<StatusEffectInstance> heartymeals$prioritizeCozy(Ordering<?> instance, Iterable<StatusEffectInstance> elements, Operation<List<StatusEffectInstance>> original) {
		return HeartyMealsClient.prioritizeCozy(instance, elements, original);
	}

	@ModifyVariable(method = "drawStatusEffects", at = @At("STORE"))
	private StatusEffectInstance heartymeals$trackCozy(StatusEffectInstance statusEffectInstance) {
		isCozy = statusEffectInstance.getEffectType() == ModStatusEffects.COZY;
		return statusEffectInstance;
	}

	@ModifyArg(method = "drawStatusEffectBackgrounds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
	private Identifier heartymeals$cozyBackgroundLarge(Identifier value) {
		if (isCozy) {
			return COZY_BACKGROUND;
		}
		return value;
	}
}
