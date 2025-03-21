/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface DisableHudRepositioningEvent {
	/**
	 * Determines whether the armor bar and air bubble bar should not be repositioned regardless of config settings. Useful if you need to append extra information where the food bar is without having things overlap each other.
	 */
	Event<DisableHudRepositioningEvent> EVENT = EventFactory.createArrayBacked(DisableHudRepositioningEvent.class, events -> player -> {
		for (DisableHudRepositioningEvent event : events) {
			if (event.shouldDisableRepositioning(player)) {
				return true;
			}
		}
		return false;
	});

	boolean shouldDisableRepositioning(PlayerEntity player);
}
