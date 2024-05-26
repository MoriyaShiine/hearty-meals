/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin.client;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(InGameHud.class)
public interface InGameHudAccessor {
	@Invoker("getRiddenEntity")
	LivingEntity heartymeals$getRiddenEntity();
}
