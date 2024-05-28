package moriyashiine.heartymeals.data.provider;

import moriyashiine.heartymeals.common.tag.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.util.Identifier.tryParse;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
	public ModItemTagProvider(FabricDataOutput output) {
		super(output, CompletableFuture.supplyAsync(BuiltinRegistries::createWrapperLookup));
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
		getOrCreateTagBuilder(ModItemTags.INCREASED_SATURATION)
				.add(Items.PUMPKIN_PIE)
				.addOptional(tryParse("bewitchment:witchberry_pie"));

		getOrCreateTagBuilder(TagKey.of(RegistryKeys.ITEM, tryParse("enchancement:cannot_assimilate")))
				.add(Items.GOLDEN_CARROT);
	}
}
