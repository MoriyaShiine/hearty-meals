/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.client;

import moriyashiine.heartymeals.client.event.RenderFoodHealingEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;

public class HeartyMealsClient implements ClientModInitializer {
	public static boolean leaveMyBarsAloneLoaded = false;

	@Override
	public void onInitializeClient() {
		leaveMyBarsAloneLoaded = FabricLoader.getInstance().isModLoaded("leavemybarsalone");
		ItemTooltipCallback.EVENT.register(new RenderFoodHealingEvent.Tooltip());
	}
}
