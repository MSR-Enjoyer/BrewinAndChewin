package umpaz.brewinandchewin.neoforge.client;

import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.utility.BnCTextUtils;
import umpaz.brewinandchewin.neoforge.client.integration.IntoxicationAppleSkinCompatNeoForge;

@EventBusSubscriber(modid = BrewinAndChewin.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class TipsyEffects {
    @SubscribeEvent
    public static void whatsYourName(RenderNameTagEvent event) {
        Component newName = BnCTextUtils.nameTagRenderer(event.getContent());
        if (event.getContent() != newName)
            event.setContent(newName);
    }

    @SubscribeEvent
    public static void iCanHear(ClientChatReceivedEvent.Player event) {
        if (BnCTextUtils.getTipsyMessage() != null)
            event.setMessage(BnCTextUtils.getTipsyMessage());
        BnCTextUtils.clearTipsyMessage();
    }

    static {
        if (ModList.get().isLoaded("appleskin"))
            IntoxicationAppleSkinCompatNeoForge.init();
    }
}