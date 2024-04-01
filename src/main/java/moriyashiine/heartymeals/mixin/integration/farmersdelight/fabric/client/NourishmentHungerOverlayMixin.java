/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.mixin.integration.farmersdelight.fabric.client;

import com.nhoryzon.mc.farmersdelight.client.gui.NourishmentHungerOverlay;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NourishmentHungerOverlay.class, remap = false)
public class NourishmentHungerOverlayMixin {
	@Inject(method = "onRender", at = @At("HEAD"), cancellable = true)
	private void heartymeals$disableNourishingOverlay(DrawContext context, CallbackInfo ci) {
		ci.cancel();
	}
}
