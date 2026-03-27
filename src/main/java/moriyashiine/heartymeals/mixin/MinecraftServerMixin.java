/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.heartymeals.mixin;

import moriyashiine.heartymeals.client.payload.SyncNaturalHealthRegenerationPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
	@Shadow
	public abstract PlayerList getPlayerList();

	@Inject(method = "onGameRuleChanged", at = @At("HEAD"))
	private <T> void heartymeals$syncNaturalHealthRegeneration(GameRule<T> rule, T value, CallbackInfo ci) {
		if (rule == GameRules.NATURAL_HEALTH_REGENERATION) {
			getPlayerList().getPlayers().forEach(foundPlayer -> SyncNaturalHealthRegenerationPayload.send(foundPlayer, (Boolean) value));
		}
	}
}
