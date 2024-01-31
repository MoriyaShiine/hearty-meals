/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.common.init;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import moriyashiine.heartymeals.common.HeartyMeals;
import moriyashiine.heartymeals.common.component.entity.FoodHealingComponent;

public class ModEntityComponents implements EntityComponentInitializer {
	public static final ComponentKey<FoodHealingComponent> FOOD_HEALING = ComponentRegistry.getOrCreate(HeartyMeals.id("food_healing"), FoodHealingComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(FOOD_HEALING, FoodHealingComponent::new, RespawnCopyStrategy.LOSSLESS_ONLY);
	}
}
