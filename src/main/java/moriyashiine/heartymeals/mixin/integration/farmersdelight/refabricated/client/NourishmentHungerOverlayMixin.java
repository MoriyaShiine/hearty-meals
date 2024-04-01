/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.mixin.integration.farmersdelight.refabricated.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.client.gui.NourishmentHungerOverlay;

@Mixin(value = NourishmentHungerOverlay.class, remap = false)
public class NourishmentHungerOverlayMixin {
	@Inject(method = "renderNourishmentOverlay", at = @At("HEAD"), cancellable = true)
	private static void heartymeals$disableNourishingOverlay(InGameHud gui, DrawContext graphics, CallbackInfo ci) {
		ci.cancel();
	}
}
