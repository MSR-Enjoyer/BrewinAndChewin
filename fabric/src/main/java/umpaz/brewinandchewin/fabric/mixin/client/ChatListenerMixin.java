package umpaz.brewinandchewin.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import umpaz.brewinandchewin.client.utility.BnCClientTextUtils;

@Mixin(ChatListener.class)
public class ChatListenerMixin {
    @ModifyArg(method = "showMessageToPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V"), index = 0)
    public Component brewinandchewin$modifyTipsyMessage(Component original, @Local(argsOnly = true) PlayerChatMessage chatMessage) {
        Component tipsyMessage = BnCClientTextUtils.getTipsyMessage();
        if (tipsyMessage != null) {
            BnCClientTextUtils.clearTipsyMessage();
            return tipsyMessage;
        }
        return original;
    }
}
