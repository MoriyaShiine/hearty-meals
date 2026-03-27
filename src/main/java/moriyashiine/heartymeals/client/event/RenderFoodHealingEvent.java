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
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import vectorwing.farmersdelight.common.block.PieBlock;

import java.text.DecimalFormat;
import java.util.List;

import static moriyashiine.heartymeals.common.component.entity.FoodHealingComponent.getModifiedSaturation;
import static moriyashiine.heartymeals.common.component.entity.FoodHealingComponent.getTicksPerHeal;

public class RenderFoodHealingEvent {
	public static class Hud {
		public static Identifier fullTexture = null, halfTexture = null;
		public static Gui.HeartType heartType;
		public static int[] xPoses = null, yPoses = null;
		public static int color = -1;

		public static void displayHealthGained(Minecraft client, GuiGraphicsExtractor graphics, Player player, float maxHealth) {
			if (ModConfig.displayHealthGained && HeartyMealsClient.naturalHealthRegeneration) {
				int health = Mth.ceil(player.getHealth());
				if (health < maxHealth) {
					int toHeal = getHealAmount(client, player);
					if (toHeal > 0) {
						color = ARGB.colorFromFloat((Mth.sin(Tick.renderTicks / 4F) + 1) / 3F, 1, 1, 1);
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
								graphics.blitSprite(
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

	public static class Tick implements ClientTickEvents.EndLevelTick {
		private static int renderTicks = 0;

		@Override
		public void onEndTick(ClientLevel level) {
			Minecraft client = Minecraft.getInstance();
			if (getHealAmount(client, client.player) == 0) {
				renderTicks = (int) -Math.TAU;
			} else {
				renderTicks++;
			}
		}
	}

	public static class Tooltip implements ItemTooltipCallback {
		@Override
		public void getTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> lines) {
			if (ModConfig.displayHealthGained && HeartyMealsClient.naturalHealthRegeneration && stack.has(DataComponents.FOOD)) {
				int healAmount = getItemHealAmount(stack);
				if (healAmount > 0) {
					float seconds = getMaximumHealTicks(stack) / 20F;
					MutableComponent text = Component.literal(DecimalFormat.getNumberInstance().format(healAmount / 2F) + " ").withStyle(ChatFormatting.GRAY);
					text.append(Component.literal("❤ ").withStyle(ChatFormatting.RED));
					if (!ModConfig.instantRegeneration) {
						text.append(Component.literal("/ " + DecimalFormat.getNumberInstance().format(seconds) + "s").withStyle(ChatFormatting.GRAY));
					}
					lines.add(1, text);
				}
			}
		}

		private static int getMaximumHealTicks(ItemStack stack) {
			return getItemHealAmount(stack) * getTicksPerHeal(getModifiedSaturation(stack, stack.get(DataComponents.FOOD).saturation()));
		}
	}

	private static int getHealAmount(Minecraft client, Player player) {
		int toHeal;
		FoodHealingComponent foodHealingComponent = ModEntityComponents.FOOD_HEALING.get(player);
		if (foodHealingComponent.getHealAmount() > 0) {
			toHeal = foodHealingComponent.getHealAmount() - foodHealingComponent.getAmountHealed();
		} else {
			toHeal = getItemHealAmount(player.getUseItem());
			if (toHeal == 0) {
				if (client.hitResult instanceof BlockHitResult blockHitResult) {
					toHeal = getBlockHealAmount(client.level.getBlockState(blockHitResult.getBlockPos()));
				}
				if (toHeal == 0) {
					toHeal = getItemHealAmount(player.getMainHandItem());
					if (toHeal == 0) {
						toHeal = getItemHealAmount(player.getOffhandItem());
					}
				}
			}
		}
		return toHeal;
	}

	private static int getBlockHealAmount(BlockState state) {
		if (state.getBlock() instanceof CakeBlock) {
			return Mth.floor(2 * ModConfig.healthGainMultiplier);
		} else if (HeartyMeals.farmersDelightLoaded && state.getBlock() instanceof PieBlock pieBlock) {
			return getItemHealAmount(pieBlock.getPieSliceItem());
		}
		return 0;
	}

	private static int getItemHealAmount(ItemStack stack) {
		if (stack.has(DataComponents.FOOD)) {
			return Mth.floor(stack.get(DataComponents.FOOD).nutrition() * ModConfig.healthGainMultiplier);
		}
		return 0;
	}
}
