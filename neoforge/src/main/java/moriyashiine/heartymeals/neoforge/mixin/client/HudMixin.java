package moriyashiine.heartymeals.neoforge.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moriyashiine.heartymeals.common.HeartyMealsConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class HudMixin {
	@Shadow
	public int rightHeight;

	@Definition(id = "leftHeight", field = "Lnet/minecraft/client/gui/Hud;leftHeight:I")
	@Expression("this.leftHeight")
	@ModifyExpressionValue(method = "extractArmorLevel", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int heartymeals$moveArmorBar(int original) {
		if (HeartyMealsConfig.moveArmorBar) {
			return rightHeight + 10;
		}
		return original;
	}

	@Inject(method = "extractArmorLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getArmorValue()I"), cancellable = true)
	private void heartymeals$moveArmorBar(GuiGraphicsExtractor graphics, CallbackInfo ci) {
		if (HeartyMealsConfig.moveArmorBar) {
			ci.cancel();
		}
	}
}
