/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.mixin.client;

import com.google.common.collect.Ordering;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moriyashiine.heartymeals.client.HeartyMealsClient;
import moriyashiine.heartymeals.common.HeartyMeals;
import moriyashiine.heartymeals.common.init.ModMobEffects;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(EffectsInInventory.class)
public class EffectsInInventoryMixin {
	@Unique
	private static final Identifier COZY_BACKGROUND = HeartyMeals.id("container/inventory/cozy_background");

	@Unique
	private static boolean isCozy = false;

	@WrapOperation(method = "extractEffects", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Ordering;sortedCopy(Ljava/lang/Iterable;)Ljava/util/List;"))
	private List<MobEffectInstance> heartymeals$prioritizeCozy(Ordering<?> instance, Iterable<MobEffectInstance> elements, Operation<List<MobEffectInstance>> original) {
		return HeartyMealsClient.prioritizeCozy(instance, elements, original);
	}

	@ModifyVariable(method = "extractEffects", at = @At("STORE"), name = "effect")
	private MobEffectInstance heartymeals$trackCozy(MobEffectInstance effect) {
		isCozy = effect.getEffect() == ModMobEffects.COZY;
		return effect;
	}

	@ModifyArg(method = "extractBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
	private Identifier heartymeals$cozyBackgroundLarge(Identifier location) {
		if (isCozy) {
			return COZY_BACKGROUND;
		}
		return location;
	}
}
