/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.common.component.entity;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import moriyashiine.heartymeals.common.init.ModEntityComponents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;

public class FoodHealingComponent implements AutoSyncedComponent, CommonTickingComponent {
	private final PlayerEntity obj;
	private boolean fromSaturation = false;
	private int healAmount = 0, ticksPerHeal = 0;
	private int healTicks = 0;
	private int amountHealed = 0;

	public FoodHealingComponent(PlayerEntity obj) {
		this.obj = obj;
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		fromSaturation = tag.getBoolean("FromSaturation");
		healAmount = tag.getInt("HealAmount");
		ticksPerHeal = tag.getInt("TicksPerHeal");
		healTicks = tag.getInt("HealTicks");
		amountHealed = tag.getInt("AmountHealed");
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		tag.putBoolean("FromSaturation", fromSaturation);
		tag.putInt("HealAmount", healAmount);
		tag.putInt("TicksPerHeal", ticksPerHeal);
		tag.putInt("HealTicks", healTicks);
		tag.putInt("AmountHealed", amountHealed);
	}

	@Override
	public void tick() {
		if (healAmount > 0) {
			healTicks++;
			if (healTicks % ticksPerHeal == 0) {
				if (!obj.hasStatusEffect(StatusEffects.HUNGER)) {
					obj.heal(1);
				}
				amountHealed++;
			}
			if (healTicks == getMaximumHealTicks()) {
				healAmount = ticksPerHeal = healTicks = amountHealed = 0;
			}
		}
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

	public void startHealing(int food, float saturationModifier) {
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
			ticksPerHeal = getTicksPerHeal(saturationModifier);
			for (Item item : Registries.ITEM) {
				if (item.isFood()) {
					obj.getItemCooldownManager().set(item, getMaximumHealTicks());
				}
			}
		}
	}

	public static int getMaximumHealTicks(FoodComponent foodComponent) {
		return foodComponent.getHunger() * getTicksPerHeal(foodComponent.getSaturationModifier());
	}

	public static int getTicksPerHeal(float saturationModifier) {
		return (int) MathHelper.clamp(20 * (1F / saturationModifier), 0, 60);
	}
}
