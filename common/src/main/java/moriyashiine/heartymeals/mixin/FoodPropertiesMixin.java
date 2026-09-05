package moriyashiine.heartymeals.mixin;

import moriyashiine.heartymeals.common.util.StewHolder;
import net.minecraft.world.food.FoodProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin implements StewHolder {
	@Unique
	private boolean isStew = false;

	@Override
	public boolean heartymeals$isStew() {
		return isStew;
	}

	@Override
	public void heartymeals$setStew(boolean stew) {
		isStew = stew;
	}
}
