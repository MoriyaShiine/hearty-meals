/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.client.event;

import moriyashiine.heartymeals.client.HeartyMealsClient;
import moriyashiine.heartymeals.common.HeartyMeals;
import moriyashiine.heartymeals.common.ModConfig;
import moriyashiine.heartymeals.common.component.entity.FoodHealingComponent;
import moriyashiine.heartymeals.common.init.ModEntityComponents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import vectorwing.farmersdelight.common.block.PieBlock;

import java.text.DecimalFormat;
import java.util.List;

import static moriyashiine.heartymeals.common.component.entity.FoodHealingComponent.getModifiedSaturation;
import static moriyashiine.heartymeals.common.component.entity.FoodHealingComponent.getTicksPerHeal;

public class RenderFoodHealingEvent {
	public static class Hud {
		public static Identifier fullTexture = null, halfTexture = null;
		public static InGameHud.HeartType heartType;
		public static int[] xPoses = null, yPoses = null;
		public static int color = -1;

		public static void displayHealthGained(MinecraftClient client, DrawContext context, PlayerEntity player, float maxHealth) {
			if (ModConfig.displayHealthGained && HeartyMealsClient.naturalRegen) {
				int health = MathHelper.ceil(player.getHealth());
				if (health < maxHealth) {
					int toHeal = getHealAmount(client, player);
					if (toHeal > 0) {
						color = ColorHelper.fromFloats((MathHelper.sin(Tick.renderTicks / 4F) + 1) / 3F, 1, 1, 1);
						for (int i = health; i < maxHealth; i++) {
							int index = i / 2;
							int currentHealth = i - health;
							if (currentHealth < toHeal) {
								boolean currentlyHalf = false;
								int xOffset = 0;
								if (i % 2 == 1) {
									xOffset = 5;
								}
								if (health % 2 != currentHealth % 2) {
									currentlyHalf = true;
								}
								context.drawGuiTexture(
										RenderPipelines.GUI_TEXTURED, currentlyHalf ? fullTexture : halfTexture,
										9, 9,
										currentlyHalf ? 5 : 0, 0,
										xPoses[index] + xOffset, yPoses[index],
										5, 9);
							}
						}
					}
				}
			}
			fullTexture = halfTexture = null;
			heartType = null;
			xPoses = yPoses = null;
			color = -1;
		}
	}

	public static class Tick implements ClientTickEvents.EndWorldTick {
		private static int renderTicks = 0;

		@Override
		public void onEndTick(ClientWorld world) {
			MinecraftClient client = MinecraftClient.getInstance();
			if (getHealAmount(client, client.player) == 0) {
				renderTicks = (int) -Math.TAU;
			} else {
				renderTicks++;
			}
		}
	}

	public static class Tooltip implements ItemTooltipCallback {
		@Override
		public void getTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> lines) {
			if (ModConfig.displayHealthGained && HeartyMealsClient.naturalRegen && stack.contains(DataComponentTypes.FOOD)) {
				int healAmount = getItemHealAmount(stack);
				if (healAmount > 0) {
					float seconds = getMaximumHealTicks(stack) / 20F;
					MutableText text = Text.literal(DecimalFormat.getNumberInstance().format(healAmount / 2F) + " ").formatted(Formatting.GRAY);
					text.append(Text.literal("❤ ").formatted(Formatting.RED));
					if (!ModConfig.instantRegeneration) {
						text.append(Text.literal("/ " + DecimalFormat.getNumberInstance().format(seconds) + "s").formatted(Formatting.GRAY));
					}
					lines.add(1, text);
				}
			}
		}

		private static int getMaximumHealTicks(ItemStack stack) {
			return getItemHealAmount(stack) * getTicksPerHeal(getModifiedSaturation(stack, stack.get(DataComponentTypes.FOOD).saturation()));
		}
	}

	private static int getHealAmount(MinecraftClient client, PlayerEntity player) {
		int toHeal;
		FoodHealingComponent foodHealingComponent = ModEntityComponents.FOOD_HEALING.get(player);
		if (foodHealingComponent.getHealAmount() > 0) {
			toHeal = foodHealingComponent.getHealAmount() - foodHealingComponent.getAmountHealed();
		} else {
			toHeal = getItemHealAmount(player.getActiveItem());
			if (toHeal == 0) {
				if (client.crosshairTarget instanceof BlockHitResult blockHitResult) {
					toHeal = getBlockHealAmount(client.world.getBlockState(blockHitResult.getBlockPos()));
				}
				if (toHeal == 0) {
					toHeal = getItemHealAmount(player.getMainHandStack());
					if (toHeal == 0) {
						toHeal = getItemHealAmount(player.getOffHandStack());
					}
				}
			}
		}
		return toHeal;
	}

	private static int getBlockHealAmount(BlockState state) {
		if (state.getBlock() instanceof CakeBlock) {
			return MathHelper.floor(2 * ModConfig.healthGainMultiplier);
		} else if (HeartyMeals.farmersDelightLoaded && state.getBlock() instanceof PieBlock pieBlock) {
			return getItemHealAmount(pieBlock.getPieSliceItem());
		}
		return 0;
	}

	private static int getItemHealAmount(ItemStack stack) {
		if (stack.contains(DataComponentTypes.FOOD)) {
			return MathHelper.floor(stack.get(DataComponentTypes.FOOD).nutrition() * ModConfig.healthGainMultiplier);
		}
		return 0;
	}
}
