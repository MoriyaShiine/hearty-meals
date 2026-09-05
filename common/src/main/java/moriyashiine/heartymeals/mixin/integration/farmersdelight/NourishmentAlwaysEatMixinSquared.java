package moriyashiine.heartymeals.mixin.integration.farmersdelight;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Player.class, priority = 1500)
public class NourishmentAlwaysEatMixinSquared {
	@TargetHandler(mixin = "vectorwing.farmersdelight.common.mixin.NourishmentAlwaysEatMixin", name = "alwaysEatUnderNourishmentEffect")
	@ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasEffect(Lnet/minecraft/core/Holder;)Z"))
	private boolean heartymeals$disableNourishmentAlwaysEat(boolean original) {
		return false;
	}
}
