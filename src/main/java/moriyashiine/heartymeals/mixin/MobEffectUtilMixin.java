/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.mixin;

import moriyashiine.heartymeals.common.init.ModMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectUtil.class)
public class MobEffectUtilMixin {
	@Unique
	private static final Component COZY_DURATION_TEXT = Component.translatable("effect.hearty-meals.cozy.description").withStyle(ChatFormatting.ITALIC);

	@Inject(method = "formatDuration", at = @At("HEAD"), cancellable = true)
	private static void heartymeals$cozyNoDuration(MobEffectInstance instance, float scale, float tickrate, CallbackInfoReturnable<Component> cir) {
		if (instance.getEffect() == ModMobEffects.COZY) {
			cir.setReturnValue(COZY_DURATION_TEXT);
		}
	}
}
