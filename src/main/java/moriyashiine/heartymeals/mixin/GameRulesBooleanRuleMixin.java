/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.heartymeals.mixin;

import com.mojang.brigadier.context.CommandContext;
import moriyashiine.heartymeals.client.packet.SyncNaturalRegenPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRules.BooleanRule.class)
public class GameRulesBooleanRuleMixin {
	@Shadow
	private boolean value;

	@Inject(method = "setFromArgument", at = @At("TAIL"))
	private void heartymeals$syncNaturalRegen(CommandContext<ServerCommandSource> context, String name, CallbackInfo ci) {
		context.getSource().getServer().getPlayerManager().getPlayerList().forEach(foundPlayer -> SyncNaturalRegenPacket.send(foundPlayer, value));
	}
}
