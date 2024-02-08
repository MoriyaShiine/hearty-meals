/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.common.init;

import moriyashiine.heartymeals.common.HeartyMeals;
import moriyashiine.heartymeals.common.statuseffect.CozyStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModStatusEffects {
	public static final StatusEffect COZY = new CozyStatusEffect(StatusEffectCategory.BENEFICIAL, 0xFF9101);

	public static void init() {
		Registry.register(Registries.STATUS_EFFECT, HeartyMeals.id("cozy"), COZY);
	}
}
