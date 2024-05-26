/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.common.init;

import moriyashiine.heartymeals.common.HeartyMeals;
import moriyashiine.heartymeals.common.component.entity.FoodHealingComponent;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public class ModEntityComponents implements EntityComponentInitializer {
	public static final ComponentKey<FoodHealingComponent> FOOD_HEALING = ComponentRegistry.getOrCreate(HeartyMeals.id("food_healing"), FoodHealingComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(FOOD_HEALING, FoodHealingComponent::new, RespawnCopyStrategy.LOSSLESS_ONLY);
	}
}
