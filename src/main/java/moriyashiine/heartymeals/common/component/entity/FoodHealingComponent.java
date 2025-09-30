/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.common.component.entity;

import moriyashiine.heartymeals.common.HeartyMeals;
import moriyashiine.heartymeals.common.ModConfig;
import moriyashiine.heartymeals.common.event.UniqueIngredientsEvent;
import moriyashiine.heartymeals.common.init.ModEntityComponents;
import moriyashiine.heartymeals.common.init.ModStatusEffects;
import moriyashiine.heartymeals.common.tag.ModBlockTags;
import moriyashiine.heartymeals.common.tag.ModItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import vectorwing.farmersdelight.common.registry.ModEffects;

import java.util.Optional;

public class FoodHealingComponent implements AutoSyncedComponent, CommonTickingComponent {
	public static float modifiedSaturation = -1;

	private final PlayerEntity obj;
	private boolean fromSaturation = false;
	private int healAmount = 0, ticksPerHeal = 0;
	private int healTicks = 0;
	private int amountHealed = 0;

	public FoodHealingComponent(PlayerEntity obj) {
		this.obj = obj;
	}

	@Override
	public void readData(ReadView readView) {
		fromSaturation = readView.getBoolean("FromSaturation", false);
		healAmount = readView.getInt("HealAmount", 0);
		ticksPerHeal = readView.getInt("TicksPerHeal", 0);
		healTicks = readView.getInt("HealTicks", 0);
		amountHealed = readView.getInt("AmountHealed", 0);
	}

	@Override
	public void writeData(WriteView writeView) {
		writeView.putBoolean("FromSaturation", fromSaturation);
		writeView.putInt("HealAmount", healAmount);
		writeView.putInt("TicksPerHeal", ticksPerHeal);
		writeView.putInt("HealTicks", healTicks);
		writeView.putInt("AmountHealed", amountHealed);
	}

	@Override
	public void tick() {
		tickFoodHealing();
		tickCampfire();
		tickNourishment();
	}

	public void sync() {
		ModEntityComponents.FOOD_HEALING.sync(obj);
	}

	public void setFromSaturation(boolean fromSaturation) {
		this.fromSaturation = fromSaturation;
	}

	public int getHealAmount() {
		return healAmount;
	}

	public int getAmountHealed() {
		return amountHealed;
	}

	public int getMaximumHealTicks() {
		return healAmount * ticksPerHeal;
	}

	public boolean canEat() {
		return healAmount == 0;
	}

	public void startHealing(int food, float saturation) {
		if (fromSaturation) {
			fromSaturation = false;
			int duration = obj.getStatusEffect(StatusEffects.SATURATION).getDuration();
			if (duration == StatusEffectInstance.INFINITE) {
				duration = obj.age;
			}
			if (duration % 2 == 0) {
				obj.heal(food);
			}
		} else if (food > 0) {
			healAmount = food;
			ticksPerHeal = getTicksPerHeal(saturation);
			for (Item item : Registries.ITEM) {
				if (item.getComponents().contains(DataComponentTypes.FOOD)) {
					obj.getItemCooldownManager().set(item.getDefaultStack(), getMaximumHealTicks());
				}
			}
			for (int i = 0; i < obj.getInventory().size(); i++) {
				ItemStack stack = obj.getInventory().getStack(i);
				if (stack.contains(DataComponentTypes.FOOD)) {
					obj.getItemCooldownManager().set(stack, getMaximumHealTicks());
				}
			}
		}
	}

	public static float getModifiedSaturation(ItemStack stack, float saturation) {
		if (stack.isIn(ModItemTags.INCREASED_SATURATION)) {
			saturation *= 2.6F;
		}
		return saturation + UniqueIngredientsEvent.getUniqueIngredients(stack.getItem()) / 2F;
	}

	public static int getTicksPerHeal(float saturation) {
		return MathHelper.floor((int) Math.max(5, MathHelper.lerp(saturation / 20, 60, 0F)) * ModConfig.regenerationTimeMultiplier);
	}

	private void tickFoodHealing() {
		if (healAmount > 0) {
			healTicks++;
			if (healTicks % ticksPerHeal == 0) {
				if (obj.getEntityWorld() instanceof ServerWorld world && !obj.hasStatusEffect(StatusEffects.HUNGER) && world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
					obj.heal(1);
				}
				amountHealed++;
			}
			if (healTicks == getMaximumHealTicks()) {
				healAmount = ticksPerHeal = healTicks = amountHealed = 0;
			}
		}
	}

	private void tickCampfire() {
		if (obj.age % 20 == 0) {
			if (ModConfig.campfireHealing) {
				Optional<BlockPos> closestCampfire = BlockPos.findClosest(obj.getBlockPos(), 5, 5, foundPos -> isCozySource(obj.getEntityWorld(), foundPos));
				if (closestCampfire.isEmpty()) {
					closestCampfire = BlockPos.findClosest(obj.getBlockPos(), 15, 15, foundPos -> isCozySource(obj.getEntityWorld(), foundPos) && ((CampfireBlock) Blocks.CAMPFIRE).isSignalFireBaseBlock(obj.getEntityWorld().getBlockState(foundPos.down())));
				}
				if (closestCampfire.isPresent()) {
					obj.addStatusEffect(new StatusEffectInstance(ModStatusEffects.COZY, StatusEffectInstance.INFINITE, 0, true, false, true));
				} else {
					obj.removeStatusEffect(ModStatusEffects.COZY);
				}
			} else {
				obj.removeStatusEffect(ModStatusEffects.COZY);
			}
		}
	}

	private void tickNourishment() {
		if (HeartyMeals.farmersDelightLoaded && obj.hasStatusEffect(ModEffects.NOURISHMENT)) {
			StatusEffectInstance instance = obj.getStatusEffect(ModEffects.NOURISHMENT);
			int duration = instance.getDuration();
			if (duration == StatusEffectInstance.INFINITE) {
				duration = obj.age;
			}
			if (duration % 200 == 0) {
				obj.heal(instance.getAmplifier() + 1);
			}
		}
	}

	private static boolean isCozySource(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.isIn(ModBlockTags.COZY_SOURCES)) {
			return !state.contains(Properties.LIT) || state.get(Properties.LIT);
		}
		return false;
	}
}
