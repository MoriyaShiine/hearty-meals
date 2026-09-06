package moriyashiine.heartymeals.common.event;

import moriyashiine.heartymeals.common.tag.HeartyMealsItemTags;
import moriyashiine.strawberrylib.api.event.FoodEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class IncreaseSaturationEvent {
	public static void init() {
		FoodEvents.MODIFY_NUTRITION.register(new Nutrition());
		FoodEvents.MODIFY_SATURATION.register(new Saturation());
	}

	private static class Nutrition implements FoodEvents.ModifyNutrition {
		@Override
		public int modify(int nutrition, Level level, Player user, ItemStack stack) {
			int uniqueIngredientBonus = Mth.floor(UniqueIngredientsEvent.getUniqueIngredients(stack.getItem()) / 2F);
			return nutrition + uniqueIngredientBonus;
		}
	}

	private static class Saturation implements FoodEvents.ModifySaturation {
		// Golden Carrot Saturation
		private static final float MAX_SATURATION = 14.4F;

		@Override
		public float modify(float saturation, Level level, Player user, ItemStack stack) {
			if (stack.is(HeartyMealsItemTags.INCREASED_SATURATION)) {
				saturation *= 3;
			}
			float cappedSaturation = Math.min(MAX_SATURATION, saturation);
			float extraSaturation = Math.max(0, saturation - MAX_SATURATION);
			float extraSaturationBonus = Math.max(0, (float) Math.log(extraSaturation));
			return cappedSaturation + extraSaturationBonus;
		}
	}
}
