/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.mixin.client;

import com.google.common.collect.Ordering;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moriyashiine.heartymeals.client.HeartyMealsClient;
import moriyashiine.heartymeals.client.event.RenderFoodHealingEvent;
import moriyashiine.heartymeals.common.ModConfig;
import moriyashiine.heartymeals.common.init.ModStatusEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
	@Unique
	private static final Identifier EMPTY_TEXTURE = new Identifier("textures/block/redstone_dust_overlay.png");

	@Unique
	private static boolean setValues = false, isCozy = false;

	@Unique
	private static int heartIndex = -1;

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	private int scaledHeight;

	@Shadow
	protected abstract PlayerEntity getCameraPlayer();

	@Shadow
	protected abstract LivingEntity getRiddenEntity();

	@Inject(method = "renderHealthBar", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/hud/InGameHud$HeartType;fromPlayerState(Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/client/gui/hud/InGameHud$HeartType;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void heartymeals$displayHealthGained(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci, InGameHud.HeartType heartType) {
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

	@Inject(method = "renderHealthBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawHeart(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/gui/hud/InGameHud$HeartType;IIIZZ)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void heartymeals$disableAbsorptionValueSetting(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci, InGameHud.HeartType heartType, int i, int j, int k, int l, int m, int n, int o, int p, int q) {
		setValues = m < j;
	}

	@Inject(method = "drawHeart", at = @At("HEAD"))
	private void heartymeals$displayHealthGained(DrawContext context, InGameHud.HeartType type, int x, int y, int v, boolean blinking, boolean halfHeart, CallbackInfo ci) {
		if (setValues && type == InGameHud.HeartType.CONTAINER) {
			RenderFoodHealingEvent.Hud.xPoses[heartIndex] = x;
			RenderFoodHealingEvent.Hud.yPoses[heartIndex] = y;
			RenderFoodHealingEvent.Hud.v = v;
			heartIndex--;
		}
	}

	@ModifyArg(method = "drawHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"))
	private Identifier heartymeals$displayHealthGained(Identifier value) {
		RenderFoodHealingEvent.Hud.texture = value;
		return value;
	}

	@WrapOperation(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Ordering;sortedCopy(Ljava/lang/Iterable;)Ljava/util/List;"))
	private List<StatusEffectInstance> heartymeals$prioritizeCozy(Ordering<?> instance, Iterable<StatusEffectInstance> elements, Operation<List<StatusEffectInstance>> original) {
		return HeartyMealsClient.prioritizeCozy(instance, elements, original);
	}

	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;getEffectType()Lnet/minecraft/entity/effect/StatusEffect;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void heartymeals$cozyBackground(DrawContext context, CallbackInfo ci, Collection collection, int i, int j, StatusEffectSpriteManager statusEffectSpriteManager, List<?> list, Iterator<?> var7, StatusEffectInstance statusEffectInstance, StatusEffect statusEffect) {
		isCozy = statusEffect == ModStatusEffects.COZY;
	}

	@ModifyArg(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 0))
	private Identifier heartymeals$cozyBackground(Identifier value) {
		if (isCozy) {
			return HeartyMealsClient.COZY_BACKGROUND;
		}
		return value;
	}

	@ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 6), index = 2)
	private int heartymeals$lowerAirBar0(int value) {
		if (getCameraPlayer().getArmor() <= 0) {
			return value + 10;
		}
		return value;
	}

	@ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 7), index = 2)
	private int heartymeals$lowerAirBar1(int value) {
		if (getCameraPlayer().getArmor() <= 0) {
			return value + 10;
		}
		return value;
	}

	@ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 3))
	private Identifier heartymeals$hideHungerBar0(Identifier value) {
		return EMPTY_TEXTURE;
	}

	@ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 4))
	private Identifier heartymeals$hideHungerBar1(Identifier value) {
		return EMPTY_TEXTURE;
	}

	@ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 5))
	private Identifier heartymeals$hideHungerBar2(Identifier value) {
		return EMPTY_TEXTURE;
	}

	@ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 0), index = 1)
	private int heartymeals$moveArmorBar0X(int value) {
		return adjustArmorX(value);
	}

	@ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 0), index = 2)
	private int heartymeals$moveArmorBar0Y(int value) {
		return adjustArmorY(value);
	}

	@ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 1), index = 1)
	private int heartymeals$moveArmorBar1X(int value) {
		return adjustArmorX(value);
	}

	@ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 1), index = 2)
	private int heartymeals$moveArmorBar1Y(int value) {
		return adjustArmorY(value);
	}

	@ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 2), index = 1)
	private int heartymeals$moveArmorBar2X(int value) {
		return adjustArmorX(value);
	}

	@ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 2), index = 2)
	private int heartymeals$moveArmorBar2Y(int value) {
		return adjustArmorY(value);
	}

	@Unique
	private int adjustArmorX(int value) {
		if (ModConfig.moveArmorBar) {
			return value + 101;
		}
		return value;
	}

	@Unique
	private int adjustArmorY(int value) {
		if (ModConfig.moveArmorBar) {
			if (!HeartyMealsClient.leaveMyBarsAloneLoaded && getRiddenEntity() != null) {
				return Integer.MAX_VALUE;
			}
			return scaledHeight - 39;
		}
		return value;
	}
}
