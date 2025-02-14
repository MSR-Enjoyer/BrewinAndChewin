package umpaz.brewinandchewin.common.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.client.multiplayer.chat.ChatTrustLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import umpaz.brewinandchewin.client.BrewinAndChewinClient;
import umpaz.brewinandchewin.client.utility.BnCTextUtils;
import umpaz.brewinandchewin.common.BnCConfiguration;
import umpaz.brewinandchewin.common.registry.BnCEffects;

import java.time.Instant;

@Mixin(ChatListener.class)
public class ChatListenerMixin {
    @ModifyReturnValue(method = "evaluateTrustLevel", at = @At("RETURN"))
    public ChatTrustLevel brewinandchewin$setTipsyChatToModified(ChatTrustLevel original, PlayerChatMessage chatMessage, Component decoratedServerContent) {
        BnCTextUtils.setupChatMessage(decoratedServerContent, chatMessage.sender());
        if (BnCTextUtils.getTipsyMessage() != null)
            return ChatTrustLevel.MODIFIED;
        return original;
    }
}
