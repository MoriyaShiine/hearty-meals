/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.common.component.entity;

import moriyashiine.heartymeals.common.HeartyMeals;
import moriyashiine.heartymeals.common.ModConfig;
import moriyashiine.heartymeals.common.init.ModEntityComponents;
import moriyashiine.heartymeals.common.init.ModStatusEffects;
import moriyashiine.heartymeals.common.tag.ModBlockTags;
import moriyashiine.heartymeals.common.tag.ModItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import vectorwing.farmersdelight.common.registry.ModEffects;

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
	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		fromSaturation = tag.getBoolean("FromSaturation");
		healAmount = tag.getInt("HealAmount");
		ticksPerHeal = tag.getInt("TicksPerHeal");
		healTicks = tag.getInt("HealTicks");
		amountHealed = tag.getInt("AmountHealed");
	}

	@Override
	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		tag.putBoolean("FromSaturation", fromSaturation);
		tag.putInt("HealAmount", healAmount);
		tag.putInt("TicksPerHeal", ticksPerHeal);
		tag.putInt("HealTicks", healTicks);
		tag.putInt("AmountHealed", amountHealed);
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
			ticksPerHeal = getTicksPerHeal(food, saturation);
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
			return saturation * 2.6F;
		}
		return saturation;
	}

	public static int getTicksPerHeal(int nutrition, float saturation) {
		float originalSaturation = saturation / nutrition / 2;
		return (int) MathHelper.clamp(20F / originalSaturation, 0, 60);
	}

	private void tickFoodHealing() {
		if (healAmount > 0) {
			healTicks++;
			if (healTicks % ticksPerHeal == 0) {
				if (obj.getWorld() instanceof ServerWorld serverWorld && !obj.hasStatusEffect(StatusEffects.HUNGER) && serverWorld.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
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
				if (BlockPos.findClosest(obj.getBlockPos(), 5, 5, foundPos -> {
					BlockState state = obj.getWorld().getBlockState(foundPos);
					if (state.isIn(ModBlockTags.COZY_SOURCES)) {
						return !state.contains(Properties.LIT) || state.get(Properties.LIT);
					}
					return false;
				}).isPresent()) {
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
}
