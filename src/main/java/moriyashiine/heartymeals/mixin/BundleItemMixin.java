/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moriyashiine.heartymeals.common.ModConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(BundleItem.class)
public class BundleItemMixin {
	@ModifyExpressionValue(method = "getItemOccupancy", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I"))
	private static int heartymeals$potionBundleStacking(int value, ItemStack stack) {
		if (ModConfig.potionBundleStacking && stack.getItem() instanceof PotionItem) {
			return value * 4;
		}
		return value;
	}

	@Inject(method = "dropAllBundledItems", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;fromNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void heartymeals$potionBundleStacking(ItemStack stack, PlayerEntity player, CallbackInfoReturnable<Boolean> cir, NbtCompound bundleNbt, NbtList inventory, int count, NbtCompound stackNbt, ItemStack droppedStack) {
		if (ModConfig.potionBundleStacking && droppedStack.getItem() instanceof PotionItem) {
			while (!droppedStack.isEmpty()) {
				player.dropItem(droppedStack.split(1), true);
			}
		}
	}

	@Inject(method = "removeFirstStack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;fromNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private static void heartymeals$potionBundleStacking(ItemStack stack, CallbackInfoReturnable<Optional<ItemStack>> cir, NbtCompound bundleNbt, NbtList inventory, int unused, NbtCompound stackNbt, ItemStack extractedStack) {
		if (ModConfig.potionBundleStacking && extractedStack.getItem() instanceof PotionItem && extractedStack.getCount() > 1) {
			ItemStack split = extractedStack.split(1);
			inventory.set(0, extractedStack.writeNbt(new NbtCompound()));
			cir.setReturnValue(Optional.of(split));
		}
	}
}
