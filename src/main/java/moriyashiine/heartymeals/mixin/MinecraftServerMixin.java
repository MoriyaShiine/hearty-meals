/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin;

import moriyashiine.heartymeals.client.payload.SyncNaturalHealthRegenerationPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
	@Shadow
	public abstract PlayerManager getPlayerManager();

	@Inject(method = "onGameRuleUpdated", at = @At("HEAD"))
	private <T> void heartymeals$syncNaturalHealthRegeneration(GameRule<T> gameRule, T object, CallbackInfo ci) {
		if (gameRule == GameRules.NATURAL_HEALTH_REGENERATION) {
			getPlayerManager().getPlayerList().forEach(foundPlayer -> SyncNaturalHealthRegenerationPayload.send(foundPlayer, (Boolean) object));
		}
	}
}
