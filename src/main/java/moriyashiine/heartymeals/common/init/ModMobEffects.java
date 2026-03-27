/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.common.init;

import moriyashiine.heartymeals.common.world.effect.CozyMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import static moriyashiine.strawberrylib.api.module.SLibRegistries.registerMobEffect;

public class ModMobEffects {
	public static final Holder<MobEffect> COZY = registerMobEffect("cozy", new CozyMobEffect(MobEffectCategory.BENEFICIAL, 0xFF9101));

	public static void init() {
	}
}
