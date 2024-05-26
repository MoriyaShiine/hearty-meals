/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import moriyashiine.heartymeals.client.HeartyMealsClient;
import moriyashiine.heartymeals.common.ModConfig;
import moriyashiine.heartymeals.common.component.entity.FoodHealingComponent;
import moriyashiine.heartymeals.common.init.ModEntityComponents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.text.DecimalFormat;
import java.util.List;

public class RenderFoodHealingEvent {
	public static class Hud {
		public static Identifier fullTexture = null, halfTexture = null;
		public static InGameHud.HeartType heartType;
		public static int[] xPoses = null, yPoses = null;

		private static int renderTicks = 0;

		public static void displayHealthGained(MinecraftClient client, DrawContext context, PlayerEntity player, float maxHealth) {
			if (ModConfig.displayHealthGained && HeartyMealsClient.naturalRegen) {
				int health = MathHelper.ceil(player.getHealth());
				if (health < maxHealth) {
					int toHeal;
					FoodHealingComponent foodHealingComponent = ModEntityComponents.FOOD_HEALING.get(player);
					if (foodHealingComponent.getHealAmount() > 0) {
						toHeal = foodHealingComponent.getHealAmount() - foodHealingComponent.getAmountHealed();
					} else {
						ItemStack stack = player.getMainHandStack();
						toHeal = getHealAmount(stack);
						if (toHeal == 0) {
							stack = player.getOffHandStack();
							toHeal = getHealAmount(stack);
						}
					}
					if (toHeal > 0) {
						if (!client.isPaused()) {
							renderTicks++;
						}
						RenderSystem.enableBlend();
						context.setShaderColor(1, 1, 1, (MathHelper.sin(renderTicks / 20F) + 1) / 3F);
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
								context.drawGuiTexture(currentlyHalf ? fullTexture : halfTexture, 9, 9, currentlyHalf ? 5 : 0, 0, xPoses[index] + xOffset, yPoses[index], 5, 9);
							}
						}
						context.setShaderColor(1, 1, 1, 1);
						RenderSystem.disableBlend();
					} else {
						renderTicks = 0;
					}
				}
			}
			fullTexture = halfTexture = null;
			heartType = null;
			xPoses = yPoses = null;
		}

		private static int getHealAmount(ItemStack stack) {
			if (stack.contains(DataComponentTypes.FOOD)) {
				return stack.get(DataComponentTypes.FOOD).nutrition();
			}
			return 0;
		}
	}

	public static class Tooltip implements ItemTooltipCallback {
		@Override
		public void getTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> lines) {
			if (ModConfig.displayHealthGained && HeartyMealsClient.naturalRegen && stack.contains(DataComponentTypes.FOOD)) {
				int healAmount = Hud.getHealAmount(stack);
				if (healAmount > 0) {
					float seconds = FoodHealingComponent.getMaximumHealTicks(stack) / 20F;
					MutableText text = Text.literal(DecimalFormat.getNumberInstance().format(healAmount / 2F) + " ").formatted(Formatting.GRAY);
					text.append(Text.literal("❤ ").formatted(Formatting.RED));
					text.append(Text.literal("/ " + DecimalFormat.getNumberInstance().format(seconds) + "s").formatted(Formatting.GRAY));
					lines.add(1, text);
				}
			}
		}
	}
}
