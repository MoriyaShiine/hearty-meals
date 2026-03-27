/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.HungerMobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HungerMobEffect.class)
public class HungerMobEffectMixin {
	@Inject(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
	private void heartymeals$hungerEffect(ServerLevel serverLevel, LivingEntity mob, int amplification, CallbackInfoReturnable<Boolean> cir) {
		if (!serverLevel.getGameRules().get(GameRules.NATURAL_HEALTH_REGENERATION)) {
			amplification++;
		}
		if (amplification > 0) {
			int duration = mob.getEffect(MobEffects.HUNGER).getDuration();
			if (duration == MobEffectInstance.INFINITE_DURATION) {
				duration = mob.tickCount;
			}
			if (duration % Math.max(1, 40 / amplification) == 0) {
				if (mob.getHealth() > 1 || serverLevel.getDifficulty() == Difficulty.HARD) {
					mob.hurtServer(serverLevel, mob.damageSources().starve(), 1);
				}
			}
		}
	}
}
