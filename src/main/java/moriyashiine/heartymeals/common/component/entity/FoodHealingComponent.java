/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.common.component.entity;

import moriyashiine.heartymeals.common.HeartyMeals;
import moriyashiine.heartymeals.common.ModConfig;
import moriyashiine.heartymeals.common.event.UniqueIngredientsEvent;
import moriyashiine.heartymeals.common.init.ModEntityComponents;
import moriyashiine.heartymeals.common.init.ModMobEffects;
import moriyashiine.heartymeals.common.tag.ModBlockTags;
import moriyashiine.heartymeals.common.tag.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import vectorwing.farmersdelight.common.registry.ModEffects;

import java.util.Optional;

public class FoodHealingComponent implements AutoSyncedComponent, CommonTickingComponent {
	public static float modifiedSaturation = -1;

	private final Player obj;
	private boolean fromSaturation = false;
	private int healAmount = 0, ticksPerHeal = 0;
	private int healTicks = 0;
	private int amountHealed = 0;

	public FoodHealingComponent(Player obj) {
		this.obj = obj;
	}

	@Override
	public void readData(ValueInput input) {
		fromSaturation = input.getBooleanOr("FromSaturation", false);
		healAmount = input.getIntOr("HealAmount", 0);
		ticksPerHeal = input.getIntOr("TicksPerHeal", 0);
		healTicks = input.getIntOr("HealTicks", 0);
		amountHealed = input.getIntOr("AmountHealed", 0);
	}

	@Override
	public void writeData(ValueOutput output) {
		output.putBoolean("FromSaturation", fromSaturation);
		output.putInt("HealAmount", healAmount);
		output.putInt("TicksPerHeal", ticksPerHeal);
		output.putInt("HealTicks", healTicks);
		output.putInt("AmountHealed", amountHealed);
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
			int duration = obj.getEffect(MobEffects.SATURATION).getDuration();
			if (duration == MobEffectInstance.INFINITE_DURATION) {
				duration = obj.tickCount;
			}
			if (duration % 2 == 0) {
				obj.heal(food);
			}
		} else if (food > 0) {
			healAmount = food;
			ticksPerHeal = getTicksPerHeal(saturation);
			for (Item item : BuiltInRegistries.ITEM) {
				if (item.components().has(DataComponents.FOOD)) {
					obj.getCooldowns().addCooldown(item.getDefaultInstance(), getMaximumHealTicks());
				}
			}
			for (int i = 0; i < obj.getInventory().getContainerSize(); i++) {
				ItemStack stack = obj.getInventory().getItem(i);
				if (stack.has(DataComponents.FOOD)) {
					obj.getCooldowns().addCooldown(stack, getMaximumHealTicks());
				}
			}
		}
	}

	public static float getModifiedSaturation(ItemStack stack, float saturation) {
		if (stack.is(ModItemTags.INCREASED_SATURATION)) {
			saturation *= 2.6F;
		}
		return saturation + UniqueIngredientsEvent.getUniqueIngredients(stack.getItem()) / 2F;
	}

	public static int getTicksPerHeal(float saturation) {
		return Mth.floor((int) Math.max(5, Mth.lerp(saturation / 20, 60, 0F)) * ModConfig.regenerationTimeMultiplier);
	}

	private void tickFoodHealing() {
		if (healAmount > 0) {
			healTicks++;
			if (healTicks % ticksPerHeal == 0) {
				if (obj.level() instanceof ServerLevel level && !obj.hasEffect(MobEffects.HUNGER) && level.getGameRules().get(GameRules.NATURAL_HEALTH_REGENERATION)) {
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
		if (obj.tickCount % 20 == 0) {
			if (ModConfig.campfireHealing) {
				Optional<BlockPos> closestCampfire = BlockPos.findClosestMatch(obj.blockPosition(), 5, 5, foundPos -> isCozySource(obj.level(), foundPos));
				if (closestCampfire.isEmpty()) {
					closestCampfire = BlockPos.findClosestMatch(obj.blockPosition(), 15, 15, foundPos -> isCozySource(obj.level(), foundPos) && ((CampfireBlock) Blocks.CAMPFIRE).isSmokeSource(obj.level().getBlockState(foundPos.below())));
				}
				if (closestCampfire.isPresent()) {
					obj.addEffect(new MobEffectInstance(ModMobEffects.COZY, MobEffectInstance.INFINITE_DURATION, 0, true, false, true));
				} else {
					obj.removeEffect(ModMobEffects.COZY);
				}
			} else {
				obj.removeEffect(ModMobEffects.COZY);
			}
		}
	}

	private void tickNourishment() {
		if (HeartyMeals.farmersDelightLoaded && obj.hasEffect(ModEffects.NOURISHMENT)) {
			MobEffectInstance effect = obj.getEffect(ModEffects.NOURISHMENT);
			int duration = effect.getDuration();
			if (duration == MobEffectInstance.INFINITE_DURATION) {
				duration = obj.tickCount;
			}
			if (duration % 200 == 0) {
				obj.heal(effect.getAmplifier() + 1);
			}
		}
	}

	private static boolean isCozySource(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.is(ModBlockTags.COZY_SOURCES)) {
			return !state.hasProperty(BlockStateProperties.LIT) || state.getValue(BlockStateProperties.LIT);
		}
		return false;
	}
}
