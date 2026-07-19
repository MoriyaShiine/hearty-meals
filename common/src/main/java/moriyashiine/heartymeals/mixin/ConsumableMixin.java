package moriyashiine.heartymeals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moriyashiine.heartymeals.common.HeartyMealsConfig;
import net.minecraft.world.item.component.Consumable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Consumable.class)
public class ConsumableMixin {
	@ModifyExpressionValue(method = "startConsuming", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/Consumable;consumeTicks()I"))
	private int heartymeals$instantConsumption(int original) {
		if (HeartyMealsConfig.instantConsumption) {
			return 0;
		}
		return original;
	}
}
