package umpaz.brewinandchewin.common.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.client.multiplayer.chat.ChatTrustLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import umpaz.brewinandchewin.client.utility.BnCClientTextUtils;

@Mixin(ChatListener.class)
public class ChatListenerMixin {
    @ModifyReturnValue(method = "evaluateTrustLevel", at = @At("RETURN"))
    public ChatTrustLevel brewinandchewin$setTipsyChatToModified(ChatTrustLevel original, PlayerChatMessage chatMessage, Component decoratedServerContent) {
        BnCClientTextUtils.setupChatMessage(decoratedServerContent, chatMessage.sender());
        if (BnCClientTextUtils.getTipsyMessage() != null)
            return ChatTrustLevel.MODIFIED;
        return original;
    }
}
