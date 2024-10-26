/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin.client;

import moriyashiine.heartymeals.client.event.RenderFoodHealingEvent;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DrawContext.class)
public class DrawContextMixin {
	@ModifyArg(method = "drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIIIIIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawSpriteRegion(Ljava/util/function/Function;Lnet/minecraft/client/texture/Sprite;IIIIIIIII)V"), index = 10)
	private int heartymeals$displayHealthGained(int color) {
		if (RenderFoodHealingEvent.Hud.color != -1) {
			return RenderFoodHealingEvent.Hud.color;
		}
		return color;
	}
}
