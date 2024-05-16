/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moriyashiine.heartymeals.common.init.ModEntityComponents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
		return canFoodHeal() && (MathHelper.ceil(getHealth()) < getMaxHealth()) && ModEntityComponents.FOOD_HEALING.get(this).canEat();
	}

	@Inject(method = "eatFood", at = @At("HEAD"), cancellable = true)
	private void heartymeals$startHealing(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
		if (!ModEntityComponents.FOOD_HEALING.get(this).canEat()) {
			cir.setReturnValue(ItemStack.EMPTY);
		}
	}
}