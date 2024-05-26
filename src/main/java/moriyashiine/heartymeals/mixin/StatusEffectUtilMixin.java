/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin;

import moriyashiine.heartymeals.common.init.ModStatusEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectUtil.class)
public class StatusEffectUtilMixin {
	@Unique
	private static final Text COZY_DURATION_TEXT = Text.translatable("effect.heartymeals.cozy.description").formatted(Formatting.ITALIC);

	@Inject(method = "getDurationText", at = @At("HEAD"), cancellable = true)
	private static void heartymeals$cozyNoDuration(StatusEffectInstance effect, float multiplier, float tickRate, CallbackInfoReturnable<Text> cir) {
		if (effect.getEffectType() == ModStatusEffects.COZY) {
			cir.setReturnValue(COZY_DURATION_TEXT);
		}
	}
}
