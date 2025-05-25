/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.common;

import eu.midnightdust.lib.config.MidnightConfig;

public class ModConfig extends MidnightConfig {
	@Entry
	public static boolean allowEatingWhenFull = false;
	@Entry
	public static boolean campfireHealing = true;
	@Entry
	public static boolean disableSprinting = false;
	@Entry
	public static boolean fasterFluidConsumption = true;
	@Entry
	public static boolean increaseHoneySaturation = true;
	@Entry
	public static boolean instantConsumption = false;
	@Entry
	public static boolean instantRegeneration = false;
	@Entry
	public static boolean potionBundleStacking = true;

	@Entry(min = 0)
	public static float healthGainMultiplier = 1;
	@Entry(min = 1)
	public static float regenerationTimeMultiplier = 1;

	@Entry(category = "client")
	public static boolean displayHealthGained = true;
	@Entry(category = "client")
	public static boolean mirrorArmorBar = false;
	@Entry(category = "client")
	public static boolean moveArmorBar = true;

	static {
		// need to do this before mod init since I mixin into certain things before then
		MidnightConfig.init(HeartyMeals.MOD_ID, ModConfig.class);
	}
}
