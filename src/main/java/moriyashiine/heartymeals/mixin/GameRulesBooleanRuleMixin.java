/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.heartymeals.mixin;

import com.mojang.brigadier.context.CommandContext;
import moriyashiine.heartymeals.client.payload.SyncNaturalRegenPayload;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRules.BooleanRule.class)
public class GameRulesBooleanRuleMixin {
	@Inject(method = "setFromArgument", at = @At("TAIL"))
	private void heartymeals$syncNaturalRegen(CommandContext<ServerCommandSource> context, String name, CallbackInfo ci) {
		boolean naturalRegen = context.getSource().getServer().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
		context.getSource().getServer().getPlayerManager().getPlayerList().forEach(foundPlayer -> SyncNaturalRegenPayload.send(foundPlayer, naturalRegen));
	}
}
