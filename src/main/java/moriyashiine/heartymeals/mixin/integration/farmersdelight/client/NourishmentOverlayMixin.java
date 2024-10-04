/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin.integration.farmersdelight.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.client.gui.HUDOverlays;

@Mixin(value = HUDOverlays.NourishmentOverlay.class, remap = false)
public class NourishmentOverlayMixin {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void heartymeals$disableNourishingOverlay(MinecraftClient minecraft, PlayerEntity player, DrawContext guiGraphics, int left, int right, int top, int guiTicks, CallbackInfo ci) {
		ci.cancel();
	}
}
