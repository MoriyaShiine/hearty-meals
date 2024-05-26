/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.common;

import eu.midnightdust.lib.config.MidnightConfig;

public class ModConfig extends MidnightConfig {
	@Entry
	public static boolean campfireHealing = true;
	@Entry
	public static boolean disableSprinting = false;
	@Entry
	public static boolean fasterFluidConsumption = true;
	@Entry
	public static boolean increaseHoneySaturation = true;
	@Entry
	public static boolean potionBundleStacking = true;

	@Entry(category = "client")
	public static boolean displayHealthGained = true;
	@Entry(category = "client")
	public static boolean moveArmorBar = true;
}
