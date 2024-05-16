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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModStatusEffects {
	public static final RegistryEntry<StatusEffect> COZY = register(HeartyMeals.id("cozy"), new CozyStatusEffect(StatusEffectCategory.BENEFICIAL, 0xFF9101));

	public static RegistryEntry<StatusEffect> register(Identifier id, StatusEffect statusEffect) {
		return Registry.registerReference(Registries.STATUS_EFFECT, id, statusEffect);
	}

	public static void init() {
	}
}
