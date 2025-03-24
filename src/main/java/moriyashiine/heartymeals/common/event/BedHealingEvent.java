/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.common.event;

import moriyashiine.heartymeals.common.init.ModStatusEffects;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class BedHealingEvent implements EntitySleepEvents.StopSleeping {
	@Override
	public void onStopSleeping(LivingEntity entity, BlockPos sleepingPos) {
		if (!entity.getWorld().isClient && entity.hasStatusEffect(ModStatusEffects.COZY)) {
			long time = entity.getWorld().getTimeOfDay() % 24000;
			if (time == 0 || time == 23461) {
				entity.heal(Integer.MAX_VALUE);
			}
		}
	}
}
