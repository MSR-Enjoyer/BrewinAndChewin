package umpaz.brewinandchewin.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.utility.BnCClientTextUtils;
import umpaz.brewinandchewin.neoforge.client.integration.IntoxicationAppleSkinCompatNeoForge;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = BrewinAndChewin.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class TipsyEffects {
    @SubscribeEvent
    public static void whatsYourName(RenderNameTagEvent event) {
//        Component newName = BnCClientTextUtils.nameTagRenderer(event.getContent());
//        if (event.getContent() != newName)
//            event.setContent(newName);
    }

    @SubscribeEvent
    public static void iCanHear(ClientChatReceivedEvent.Player event) {
        BnCClientTextUtils.setupChatMessage(event.getPlayerChatMessage().withUnsignedContent(getChatMessage(event.getMessage())));
        PlayerChatMessage tipsyMessage = BnCClientTextUtils.getTipsyMessage();
        if (tipsyMessage != null && event.getBoundChatType() != null) {
            ChatType.Bound newBound = new ChatType.Bound(Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.CHAT_TYPE).getOrThrow(ChatType.CHAT), getPlayerNameComponent(event.getBoundChatType().name()), Optional.empty());
            event.setMessage(newBound.decorate(tipsyMessage.decoratedContent()));
        }
        BnCClientTextUtils.clearTipsyMessage();
    }

    private static Component getPlayerNameComponent(Component component) {
        return component.getSiblings().getFirst();
    }

    private static Component getChatMessage(Component component) {
        return (Component)((TranslatableContents)component.getContents()).getArgs()[1];
    }

    static {
        if (ModList.get().isLoaded("appleskin"))
            IntoxicationAppleSkinCompatNeoForge.init();
    }
}