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
import moriyashiine.heartymeals.common.init.ModStatusEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
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

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
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
	private MinecraftClient client;

	@Shadow
	@Nullable
	protected abstract PlayerEntity getCameraPlayer();

	@Inject(method = "render", at = @At("HEAD"))
	private void heartymeals$disableHudRepositioning(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		disableHudRepositioning = DisableHudRepositioningEvent.EVENT.invoker().shouldDisableRepositioning(getCameraPlayer());
	}

	@Inject(method = "renderHealthBar", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerEntity;getEntityWorld()Lnet/minecraft/world/World;"))
	private void heartymeals$displayHealthGained(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci, @Local InGameHud.HeartType heartType) {
		RenderFoodHealingEvent.Hud.xPoses = new int[MathHelper.ceil(maxHealth / 2F)];
		RenderFoodHealingEvent.Hud.yPoses = new int[RenderFoodHealingEvent.Hud.xPoses.length];
		RenderFoodHealingEvent.Hud.heartType = heartType;
		heartIndex = RenderFoodHealingEvent.Hud.xPoses.length - 1;
		setValues = true;
	}

	@Inject(method = "renderHealthBar", at = @At("TAIL"))
	private void heartymeals$displayHealthGained(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
		RenderFoodHealingEvent.Hud.displayHealthGained(client, context, player, maxHealth);
	}

	@Inject(method = "renderHealthBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawHeart(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/gui/hud/InGameHud$HeartType;IIZZZ)V", ordinal = 0))
	private void heartymeals$disableAbsorptionValueSetting(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci, @Local(ordinal = 7) int i, @Local(ordinal = 10) int l) {
		setValues = l < i;
	}

	@Inject(method = "drawHeart", at = @At("HEAD"))
	private void heartymeals$displayHealthGained(DrawContext context, InGameHud.HeartType type, int x, int y, boolean hardcore, boolean blinking, boolean half, CallbackInfo ci) {
		if (setValues && type == InGameHud.HeartType.CONTAINER) {
			RenderFoodHealingEvent.Hud.xPoses[heartIndex] = x;
			RenderFoodHealingEvent.Hud.yPoses[heartIndex] = y;
			heartIndex--;
		}
	}

	@WrapOperation(method = "drawHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud$HeartType;getTexture(ZZZ)Lnet/minecraft/util/Identifier;"))
	private Identifier heartymeals$displayHealthGained(InGameHud.HeartType instance, boolean hardcore, boolean half, boolean blinking, Operation<Identifier> original) {
		Identifier value = original.call(instance, hardcore, half, blinking);
		Identifier other = original.call(instance, hardcore, !half, blinking);
		if (half) {
			RenderFoodHealingEvent.Hud.fullTexture = other;
			RenderFoodHealingEvent.Hud.halfTexture = value;
		} else {
			RenderFoodHealingEvent.Hud.fullTexture = value;
			RenderFoodHealingEvent.Hud.halfTexture = other;
		}
		return value;
	}

	@WrapOperation(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Ordering;sortedCopy(Ljava/lang/Iterable;)Ljava/util/List;"))
	private List<StatusEffectInstance> heartymeals$prioritizeCozy(Ordering<?> instance, Iterable<StatusEffectInstance> elements, Operation<List<StatusEffectInstance>> original) {
		return HeartyMealsClient.prioritizeCozy(instance, elements, original);
	}

	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;shouldShowIcon()Z"))
	private void heartymeals$cozyBackground(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci, @Local RegistryEntry<?> registryEntry) {
		isCozy = registryEntry == ModStatusEffects.COZY;
	}

	@ModifyArg(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
	private Identifier heartymeals$cozyBackground(Identifier value) {
		if (isCozy) {
			return COZY_BACKGROUND_AMBIENT;
		}
		return value;
	}

	@ModifyVariable(method = "getAirBubbleY", at = @At("HEAD"), ordinal = 1, argsOnly = true)
	private int heartymeals$lowerAirBubbles(int value) {
		if (!disableHudRepositioning) {
			if (!ModConfig.moveArmorBar || getCameraPlayer().getArmor() == 0) {
				return value + 10;
			}
		}
		return value;
	}

	@Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
	private void heartymeals$hideHungerBar(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
		ci.cancel();
	}

	@ModifyVariable(method = "renderArmor", at = @At("STORE"), ordinal = 7)
	private static int heartymeals$mirrorArmorBar(int value, DrawContext context, PlayerEntity player, int i, int j, int k, int x, @Local(ordinal = 6) int n) {
		if (ModConfig.mirrorArmorBar) {
			return x + (9 - n) * 8;
		}
		return value;
	}

	@ModifyArg(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V", ordinal = 1))
	private static Identifier heartymeals$mirrorArmorBar(Identifier value) {
		if (ModConfig.mirrorArmorBar) {
			return ARMOR_HALF_MIRRORED;
		}
		return value;
	}

	@WrapOperation(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
	private static void heartymeals$moveArmorBar(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original) {
		original.call(instance, pipeline, sprite, adjustArmorX(x), adjustArmorY(y), width, height);
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
			if (!HeartyMealsClient.leaveMyBarsAloneLoaded && MinecraftClient.getInstance().inGameHud.getRiddenEntity() != null) {
				return Integer.MIN_VALUE;
			}
			return value + 10;
		}
		return value;
	}
}
