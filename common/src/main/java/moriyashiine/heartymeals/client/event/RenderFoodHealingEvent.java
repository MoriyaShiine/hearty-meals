package moriyashiine.heartymeals.client.event;

import moriyashiine.heartymeals.client.HeartyMealsClient;
import moriyashiine.heartymeals.common.HeartyMeals;
import moriyashiine.heartymeals.common.HeartyMealsConfig;
import moriyashiine.heartymeals.common.component.entity.FoodHealingComponent;
import moriyashiine.heartymeals.common.init.HeartyMealsEntityComponents;
import moriyashiine.strawberrylib.api.event.FoodEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import vectorwing.farmersdelight.common.block.PieBlock;

import java.text.DecimalFormat;
import java.util.List;

import static moriyashiine.heartymeals.common.component.entity.FoodHealingComponent.getTicksPerHeal;

public class RenderFoodHealingEvent {
	public static void init() {
		ClientTickEvents.END_LEVEL_TICK.register(new Tick());
		ItemTooltipCallback.EVENT.register(new Tooltip());
	}

	public static class Hearts {
		public static Identifier fullTexture = null, halfTexture = null;
		public static Hud.HeartType heartType;
		public static int[] xPoses = null, yPoses = null;
		public static int color = -1;

		public static void displayHealthGained(Minecraft client, GuiGraphicsExtractor graphics, Player player, float maxHealth) {
			if (HeartyMealsConfig.displayHealthGained && HeartyMealsClient.naturalHealthRegeneration) {
				int health = Mth.ceil(player.getHealth());
				if (health < maxHealth) {
					int toHeal = getHealAmount(client, player, client.level);
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

	private static class Tick implements ClientTickEvents.EndLevelTick {
		private final Minecraft client = Minecraft.getInstance();

		private static int renderTicks = 0;

		@Override
		public void onEndTick(ClientLevel level) {
			if (getHealAmount(client, client.player, client.level) == 0) {
				renderTicks = (int) -Math.TAU;
			} else {
				renderTicks++;
			}
		}
	}

	private static class Tooltip implements ItemTooltipCallback {
		private final Minecraft client = Minecraft.getInstance();

		@Override
		public void getTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> lines) {
			if (HeartyMealsConfig.displayHealthGained && HeartyMealsClient.naturalHealthRegeneration && stack.has(DataComponents.FOOD)) {
				int healAmount = getItemHealAmount(client.player, client.level, stack);
				if (healAmount > 0) {
					float seconds = getMaximumHealTicks(healAmount, client.player, client.level, stack) / 20F;
					MutableComponent text = Component.literal(DecimalFormat.getNumberInstance().format(healAmount / 2F) + " ").withStyle(ChatFormatting.GRAY);
					text.append(Component.literal("❤ ").withStyle(ChatFormatting.RED));
					if (!HeartyMealsConfig.instantRegeneration) {
						text.append(Component.literal("/ " + DecimalFormat.getNumberInstance().format(seconds) + "s").withStyle(ChatFormatting.GRAY));
					}
					lines.add(1, text);
				}
			}
		}

		private static int getMaximumHealTicks(int healAmount, Player player, Level level, ItemStack stack) {
			float saturation = stack.get(DataComponents.FOOD).saturation();
			saturation = FoodEvents.MODIFY_SATURATION.invoker().modify(saturation, level, player, stack);
			return healAmount * getTicksPerHeal(saturation);
		}
	}

	private static int getHealAmount(Minecraft client, Player player, Level level) {
		int toHeal;
		FoodHealingComponent foodHealing = HeartyMealsEntityComponents.FOOD_HEALING.get(player);
		if (foodHealing.getHealAmount() > 0) {
			toHeal = foodHealing.getHealAmount() - foodHealing.getAmountHealed();
		} else {
			toHeal = getItemHealAmount(player, level, player.getUseItem());
			if (toHeal == 0) {
				if (client.hitResult instanceof BlockHitResult blockHitResult) {
					toHeal = getBlockHealAmount(player, level, level.getBlockState(blockHitResult.getBlockPos()));
				}
				if (toHeal == 0) {
					toHeal = getItemHealAmount(player, level, player.getMainHandItem());
					if (toHeal == 0) {
						toHeal = getItemHealAmount(player, level, player.getOffhandItem());
					}
				}
			}
		}
		return toHeal;
	}

	private static int getBlockHealAmount(Player player, Level level, BlockState state) {
		if (state.getBlock() instanceof CakeBlock) {
			return getItemHealAmount(player, level, Items.CAKE.getDefaultInstance());
		} else if (HeartyMeals.farmersDelightLoaded && state.getBlock() instanceof PieBlock pieBlock) {
			return getItemHealAmount(player, level, pieBlock.getPieSliceItem());
		}
		return 0;
	}

	private static int getItemHealAmount(Player player, Level level, ItemStack stack) {
		int nutrition = 0;
		if (stack.has(DataComponents.FOOD)) {
			nutrition = stack.get(DataComponents.FOOD).nutrition();
		} else if (stack.is(Items.CAKE)) {
			nutrition = 2;
		}
		if (nutrition != 0) {
			nutrition = FoodEvents.MODIFY_NUTRITION.invoker().modify(nutrition, level, player, stack);
			return Mth.floor(nutrition * HeartyMealsConfig.healthGainMultiplier);
		}
		return 0;
	}
}
