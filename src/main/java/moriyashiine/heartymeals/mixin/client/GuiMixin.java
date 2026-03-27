/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.mixin.client;

import com.google.common.collect.Ordering;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import moriyashiine.heartymeals.api.event.DisableHudRepositioningEvent;
import moriyashiine.heartymeals.client.HeartyMealsClient;
import moriyashiine.heartymeals.client.event.RenderFoodHealingEvent;
import moriyashiine.heartymeals.common.HeartyMeals;
import moriyashiine.heartymeals.common.ModConfig;
import moriyashiine.heartymeals.common.init.ModMobEffects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Gui.class)
public abstract class GuiMixin {
	@Unique
	private static final Identifier COZY_BACKGROUND_AMBIENT = HeartyMeals.id("hud/cozy_background_ambient");

	@Unique
	private static final Identifier ARMOR_HALF_MIRRORED = HeartyMeals.id("hud/armor_half_mirrored");

	@Unique
	private static boolean setValues = false, isCozy = false, disableHudRepositioning = false;

	@Unique
	private static int heartIndex = -1;

	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	@Nullable
	protected abstract Player getCameraPlayer();

	@Inject(method = "extractRenderState", at = @At("HEAD"))
	private void heartymeals$disableHudRepositioning(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		disableHudRepositioning = DisableHudRepositioningEvent.EVENT.invoker().shouldDisableRepositioning(getCameraPlayer());
	}

	@Inject(method = "extractHearts", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Player;level()Lnet/minecraft/world/level/Level;"))
	private void heartymeals$displayHealthGained(GuiGraphicsExtractor graphics, Player player, int xLeft, int yLineBase, int healthRowHeight, int heartOffsetIndex, float maxHealth, int currentHealth, int oldHealth, int absorption, boolean blink, CallbackInfo ci, @Local(name = "type") Gui.HeartType type) {
		RenderFoodHealingEvent.Hud.xPoses = new int[Mth.ceil(maxHealth / 2F)];
		RenderFoodHealingEvent.Hud.yPoses = new int[RenderFoodHealingEvent.Hud.xPoses.length];
		RenderFoodHealingEvent.Hud.heartType = type;
		heartIndex = RenderFoodHealingEvent.Hud.xPoses.length - 1;
		setValues = true;
	}

	@Inject(method = "extractHearts", at = @At("TAIL"))
	private void heartymeals$displayHealthGained(GuiGraphicsExtractor graphics, Player player, int xLeft, int yLineBase, int healthRowHeight, int heartOffsetIndex, float maxHealth, int currentHealth, int oldHealth, int absorption, boolean blink, CallbackInfo ci) {
		RenderFoodHealingEvent.Hud.displayHealthGained(minecraft, graphics, player, maxHealth);
	}

	@Inject(method = "extractHearts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;extractHeart(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Gui$HeartType;IIZZZ)V", ordinal = 0))
	private void heartymeals$disableAbsorptionValueSetting(CallbackInfo ci, @Local(name = "healthContainerCount") int healthContainerCount, @Local(name = "containerIndex") int containerIndex) {
		setValues = containerIndex < healthContainerCount;
	}

	@Inject(method = "extractHeart", at = @At("HEAD"))
	private void heartymeals$displayHealthGained(GuiGraphicsExtractor graphics, Gui.HeartType type, int xo, int yo, boolean isHardcore, boolean blinks, boolean half, CallbackInfo ci) {
		if (setValues && type == Gui.HeartType.CONTAINER) {
			RenderFoodHealingEvent.Hud.xPoses[heartIndex] = xo;
			RenderFoodHealingEvent.Hud.yPoses[heartIndex] = yo;
			heartIndex--;
		}
	}

	@WrapOperation(method = "extractHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui$HeartType;getSprite(ZZZ)Lnet/minecraft/resources/Identifier;"))
	private Identifier heartymeals$displayHealthGained(Gui.HeartType instance, boolean isHardcore, boolean isHalf, boolean isBlink, Operation<Identifier> original) {
		Identifier value = original.call(instance, isHardcore, isHalf, isBlink);
		Identifier other = original.call(instance, isHardcore, !isHalf, isBlink);
		if (isHalf) {
			RenderFoodHealingEvent.Hud.fullTexture = other;
			RenderFoodHealingEvent.Hud.halfTexture = value;
		} else {
			RenderFoodHealingEvent.Hud.fullTexture = value;
			RenderFoodHealingEvent.Hud.halfTexture = other;
		}
		return value;
	}

	@WrapOperation(method = "extractEffects", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Ordering;sortedCopy(Ljava/lang/Iterable;)Ljava/util/List;"))
	private List<MobEffectInstance> heartymeals$prioritizeCozy(Ordering<?> instance, Iterable<MobEffectInstance> elements, Operation<List<MobEffectInstance>> original) {
		return HeartyMealsClient.prioritizeCozy(instance, elements, original);
	}

	@Inject(method = "extractEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;showIcon()Z"))
	private void heartymeals$cozyBackground(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci, @Local(name = "effect") Holder<?> effect) {
		isCozy = effect == ModMobEffects.COZY;
	}

	@ModifyArg(method = "extractEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
	private Identifier heartymeals$cozyBackground(Identifier location) {
		if (isCozy) {
			return COZY_BACKGROUND_AMBIENT;
		}
		return location;
	}

	@ModifyVariable(method = "getAirBubbleYLine", at = @At("HEAD"), argsOnly = true, ordinal = 1)
	private int heartymeals$lowerAirBubbles(int yLineAir) {
		if (!disableHudRepositioning) {
			if (!ModConfig.moveArmorBar || getCameraPlayer().getArmorValue() == 0) {
				return yLineAir + 10;
			}
		}
		return yLineAir;
	}

	@Inject(method = "extractFood", at = @At("HEAD"), cancellable = true)
	private void heartymeals$hideHungerBar(GuiGraphicsExtractor graphics, Player player, int yLineBase, int xRight, CallbackInfo ci) {
		ci.cancel();
	}

	@ModifyVariable(method = "extractArmor", at = @At("HEAD"), argsOnly = true, ordinal = 2)
	private static int heartymeals$mirrorArmorBar(int healthRowHeight) {
		if (!disableHudRepositioning && ModConfig.moveArmorBar) {
			return 0;
		}
		return healthRowHeight;
	}

	@ModifyVariable(method = "extractArmor", at = @At("STORE"), name = "xo")
	private static int heartymeals$mirrorArmorBar(int xo, @Local(argsOnly = true, ordinal = 3) int xLeft, @Local(name = "i") int i) {
		if (ModConfig.mirrorArmorBar) {
			return xLeft + (9 - i) * 8;
		}
		return xo;
	}

	@ModifyArg(method = "extractArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 1))
	private static Identifier heartymeals$mirrorArmorBar(Identifier location) {
		if (ModConfig.mirrorArmorBar) {
			return ARMOR_HALF_MIRRORED;
		}
		return location;
	}

	@WrapOperation(method = "extractArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
	private static void heartymeals$moveArmorBar(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier location, int x, int y, int width, int height, Operation<Void> original) {
		original.call(instance, renderPipeline, location, adjustArmorX(x), adjustArmorY(y), width, height);
	}

	@Unique
	private static int adjustArmorX(int value) {
		if (!disableHudRepositioning && ModConfig.moveArmorBar) {
			return value + 101;
		}
		return value;
	}

	@Unique
	private static int adjustArmorY(int value) {
		if (!disableHudRepositioning && ModConfig.moveArmorBar) {
			if (!HeartyMealsClient.leaveMyBarsAloneLoaded && Minecraft.getInstance().gui.getPlayerVehicleWithHealth() != null) {
				return Integer.MIN_VALUE;
			}
			return value + 10;
		}
		return value;
	}
}
