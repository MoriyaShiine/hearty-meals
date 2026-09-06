package moriyashiine.heartymeals.neoforge.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import moriyashiine.heartymeals.common.HeartyMealsConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
	@Shadow
	public int rightHeight;

	@Definition(id = "leftHeight", field = "Lnet/minecraft/client/gui/Gui;leftHeight:I")
	@Expression("this.leftHeight")
	@ModifyExpressionValue(method = "extractArmorLevel", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int heartymeals$moveArmorBar(int original) {
		if (HeartyMealsConfig.moveArmorBar) {
			return rightHeight + 10;
		}
		return original;
	}

	@Inject(method = "extractArmorLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getArmorValue()I"), cancellable = true)
	private void heartymeals$moveArmorBar(GuiGraphicsExtractor graphics, CallbackInfo ci, @Local(name = "player") Player player) {
		if (HeartyMealsConfig.moveArmorBar) {
			ci.cancel();
		}
	}
}
