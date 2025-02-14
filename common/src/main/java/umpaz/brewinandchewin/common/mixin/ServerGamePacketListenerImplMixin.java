package umpaz.brewinandchewin.common.mixin;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.BnCConfiguration;
import umpaz.brewinandchewin.common.network.clientbound.MakeNextPlayerChatTipsyClientboundPacket;
import umpaz.brewinandchewin.common.registry.BnCEffects;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "sendPlayerChatMessage", at = @At("HEAD"))
    public void brewinandchewin$setupModifiedChatMessage(PlayerChatMessage chatMessage, ChatType.Bound boundType, CallbackInfo ci) {
        ServerPlayer sender = player.getServer().getPlayerList().getPlayer(chatMessage.sender());
        if (sender.hasEffect(BnCEffects.TIPSY) && sender.getEffect(BnCEffects.TIPSY).getAmplifier() >= BnCConfiguration.COMMON_CONFIG.get().root().levelChatScramble()) {
            BrewinAndChewin.getHelper().sendClientbound(player, new MakeNextPlayerChatTipsyClientboundPacket(sender.getEffect(BnCEffects.TIPSY).getAmplifier()));
        }
    }
}
