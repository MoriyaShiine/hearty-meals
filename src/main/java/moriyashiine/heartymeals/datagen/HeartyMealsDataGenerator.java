/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.datagen;

import moriyashiine.heartymeals.datagen.provider.HeartyMealsBlockTagsProvider;
import moriyashiine.heartymeals.datagen.provider.HeartyMealsItemTagsProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class HeartyMealsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(HeartyMealsBlockTagsProvider::new);
		pack.addProvider(HeartyMealsItemTagsProvider::new);
	}
}
