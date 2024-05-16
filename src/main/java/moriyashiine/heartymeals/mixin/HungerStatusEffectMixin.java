/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.HungerStatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HungerStatusEffect.class)
public class HungerStatusEffectMixin {
	@Inject(method = "applyUpdateEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V", shift = At.Shift.BEFORE))
	private void heartymeals$hungerEffect(LivingEntity entity, int amplifier, CallbackInfoReturnable<Boolean> cir) {
		if (!entity.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
			amplifier++;
		}
		if (amplifier > 0) {
			int duration = entity.getStatusEffect(StatusEffects.HUNGER).getDuration();
			if (duration == StatusEffectInstance.INFINITE) {
				duration = entity.age;
			}
			if (duration % Math.max(1, 40 / amplifier) == 0) {
				if (entity.getHealth() > 1 || entity.getWorld().getDifficulty() == Difficulty.HARD) {
					entity.damage(entity.getDamageSources().starve(), 1);
				}
			}
		}
	}
}
