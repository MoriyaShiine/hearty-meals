/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.common.event;

import moriyashiine.heartymeals.common.init.ModStatusEffects;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class BedHealingEvent implements EntitySleepEvents.StopSleeping {
	@Override
	public void onStopSleeping(LivingEntity entity, BlockPos sleepingPos) {
		if (!entity.getWorld().isClient && entity.getWorld().getTimeOfDay() % 24000 == 0 && entity.hasStatusEffect(ModStatusEffects.COZY)) {
			entity.heal(Integer.MAX_VALUE);
		}
	}
}
