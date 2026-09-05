package moriyashiine.heartymeals.common.event;

import moriyashiine.heartymeals.common.tag.HeartyMealsItemTags;
import moriyashiine.strawberrylib.api.event.FoodEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class IncreaseSaturationEvent implements FoodEvents.ModifySaturation {
	public static void init() {
		FoodEvents.MODIFY_SATURATION.register(new IncreaseSaturationEvent());
	}

	@Override
	public float modify(float saturation, Level level, Player user, ItemStack stack) {
		if (stack.is(HeartyMealsItemTags.INCREASED_SATURATION)) {
			saturation *= 2.6F;
		}
		return saturation + UniqueIngredientsEvent.getUniqueIngredients(stack.getItem()) / 2F;
	}
}
