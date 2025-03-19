/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.common.init;

import moriyashiine.heartymeals.common.statuseffect.CozyStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.entry.RegistryEntry;

import static moriyashiine.strawberrylib.api.module.SLibRegistries.registerStatusEffect;

public class ModStatusEffects {
	public static final RegistryEntry<StatusEffect> COZY = registerStatusEffect("cozy", new CozyStatusEffect(StatusEffectCategory.BENEFICIAL, 0xFF9101));

	public static void init() {
	}
}
