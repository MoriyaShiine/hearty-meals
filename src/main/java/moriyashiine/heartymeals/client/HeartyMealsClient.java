/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.client;

import moriyashiine.heartymeals.client.event.DisableSprintingClientEvent;
import moriyashiine.heartymeals.client.event.RenderFoodHealingEvent;
import moriyashiine.heartymeals.client.packet.ForceDisableSprintingPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

public class HeartyMealsClient implements ClientModInitializer {
	public static boolean forceDisableSprinting = false;

	public static boolean leaveMyBarsAloneLoaded = false;

	@Override
	public void onInitializeClient() {
		leaveMyBarsAloneLoaded = FabricLoader.getInstance().isModLoaded("leavemybarsalone");
		ClientPlayNetworking.registerGlobalReceiver(ForceDisableSprintingPacket.ID, new ForceDisableSprintingPacket.Receiver());
		ClientPlayConnectionEvents.DISCONNECT.register(new DisableSprintingClientEvent());
		ItemTooltipCallback.EVENT.register(new RenderFoodHealingEvent.Tooltip());
	}
}
