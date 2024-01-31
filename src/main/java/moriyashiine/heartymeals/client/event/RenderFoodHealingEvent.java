/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import moriyashiine.heartymeals.common.ModConfig;
import moriyashiine.heartymeals.common.component.entity.FoodHealingComponent;
import moriyashiine.heartymeals.common.init.ModEntityComponents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
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
		public static Identifier texture = null;
		public static InGameHud.HeartType heartType;
		public static int[] xPoses = null, yPoses = null;
		public static int v = 0;

		private static int renderTicks = 0;

		public static void displayHealthGained(MinecraftClient client, DrawContext context, PlayerEntity player, float maxHealth) {
			if (ModConfig.displayHealthGained) {
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
								int xOffset = 0, uOffset = 0;
								if (i % 2 == 1) {
									xOffset = 5;
								}
								if (health % 2 != currentHealth % 2) {
									uOffset = -4;
								}
								context.drawTexture(texture, xPoses[index] + xOffset, yPoses[index], heartType.getU(true, false) + uOffset, v, 5, 8);
							}
						}
						context.setShaderColor(1, 1, 1, 1);
						RenderSystem.disableBlend();
					} else {
						renderTicks = 0;
					}
				}
			}
			texture = null;
			heartType = null;
			xPoses = yPoses = null;
			v = 0;
		}

		private static int getHealAmount(ItemStack stack) {
			if (stack.isFood()) {
				return stack.getItem().getFoodComponent().getHunger();
			}
			return 0;
		}
	}

	public static class Tooltip implements ItemTooltipCallback {
		@Override
		public void getTooltip(ItemStack stack, TooltipContext context, List<Text> lines) {
			if (ModConfig.displayHealthGained && stack.isFood()) {
				int healAmount = Hud.getHealAmount(stack);
				if (healAmount > 0) {
					float seconds = FoodHealingComponent.getMaximumHealTicks(stack.getItem().getFoodComponent()) / 20F;
					MutableText text = Text.literal(DecimalFormat.getNumberInstance().format(healAmount / 2F) + " ").formatted(Formatting.GRAY);
					text.append(Text.literal("❤ ").formatted(Formatting.RED));
					text.append(Text.literal("/ " + DecimalFormat.getNumberInstance().format(seconds) + "s").formatted(Formatting.GRAY));
					lines.add(1, text);
				}
			}
		}
	}
}
