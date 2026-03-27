/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.common.event;

import moriyashiine.heartymeals.common.init.ModMobEffects;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class BedHealingEvent implements EntitySleepEvents.StopSleeping {
	@Override
	public void onStopSleeping(LivingEntity entity, BlockPos sleepingPos) {
		if (entity instanceof ServerPlayer player && player.hasEffect(ModMobEffects.COZY) && player.isSleepingLongEnough()) {
			entity.heal(Integer.MAX_VALUE);
		}
	}
}
