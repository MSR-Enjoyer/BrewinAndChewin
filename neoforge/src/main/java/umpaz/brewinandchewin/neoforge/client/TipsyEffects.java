package umpaz.brewinandchewin.neoforge.client;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.utility.BnCClientTextUtils;
import umpaz.brewinandchewin.neoforge.client.integration.IntoxicationAppleSkinCompatNeoForge;

@EventBusSubscriber(modid = BrewinAndChewin.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class TipsyEffects {
    @SubscribeEvent
    public static void whatsYourName(RenderNameTagEvent event) {
        Component newName = BnCClientTextUtils.nameTagRenderer(event.getContent());
        if (event.getContent() != newName)
            event.setContent(newName);
    }

    @SubscribeEvent
    public static void iCanHear(ClientChatReceivedEvent.Player event) {
        BnCClientTextUtils.setupChatMessage(event.getPlayerChatMessage().withUnsignedContent(event.getMessage()));
        PlayerChatMessage chatMessage = BnCClientTextUtils.getTipsyMessage();
        if (chatMessage != null && chatMessage.filterMask().isEmpty() && !chatMessage.decoratedContent().equals(event.getPlayerChatMessage().decoratedContent()) && event.getBoundChatType() != null)
            event.setMessage(event.getBoundChatType().decorate(chatMessage.decoratedContent()));
        BnCClientTextUtils.clearTipsyMessage();
    }

    static {
        if (ModList.get().isLoaded("appleskin"))
            IntoxicationAppleSkinCompatNeoForge.init();
    }
}