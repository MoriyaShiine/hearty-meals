/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.mixin;

import moriyashiine.heartymeals.common.component.entity.FoodHealingComponent;
import moriyashiine.heartymeals.common.init.ModEntityComponents;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
	@Unique
	private PlayerEntity cachedPlayer = null;

	@Shadow
	public abstract void setFoodLevel(int foodLevel);

	@Shadow
	public abstract void setSaturationLevel(float saturationLevel);

	@Shadow
	public abstract void setExhaustion(float exhaustion);

	@Inject(method = "update", at = @At("HEAD"))
	private void heartymeals$forceFullHunger(PlayerEntity player, CallbackInfo ci) {
		cachedPlayer = player;
		setFoodLevel(20);
		setSaturationLevel(20);
		setExhaustion(0);
	}

	@Inject(method = "getFoodLevel", at = @At("HEAD"), cancellable = true)
	private void heartymeals$treatAsHealth(CallbackInfoReturnable<Integer> cir) {
		if (cachedPlayer != null) {
			cir.setReturnValue((int) (cachedPlayer.getHealth() / cachedPlayer.getMaxHealth() * 20));
		}
	}

	@Inject(method = "add", at = @At("HEAD"))
	private void heartymeals$treatAsHealth(int food, float saturationModifier, CallbackInfo ci) {
		if (cachedPlayer != null && !cachedPlayer.getWorld().isClient) {
			FoodHealingComponent foodHealingComponent = ModEntityComponents.FOOD_HEALING.get(cachedPlayer);
			foodHealingComponent.startHealing(food, saturationModifier);
			foodHealingComponent.sync();
		}
	}
}
