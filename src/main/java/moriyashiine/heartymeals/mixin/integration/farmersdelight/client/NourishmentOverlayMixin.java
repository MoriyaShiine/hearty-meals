/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.mixin.integration.farmersdelight.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.client.gui.HUDOverlays;

@Mixin(HUDOverlays.NourishmentOverlay.class)
public class NourishmentOverlayMixin {
	@ModifyReturnValue(method = "shouldRenderOverlay", at = @At("RETURN"))
	private boolean heartymeals$disableNourishingOverlay(boolean original) {
		return false;
	}
}
