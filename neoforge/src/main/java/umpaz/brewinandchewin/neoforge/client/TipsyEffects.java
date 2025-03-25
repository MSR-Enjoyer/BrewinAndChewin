package umpaz.brewinandchewin.neoforge.client;

import net.minecraft.ChatFormatting;
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
            event.setMessage(getPlayerNameComponent(event.getBoundChatType().name(), true).copy().append(tipsyMessage.decoratedContent()));
        }
        BnCClientTextUtils.clearTipsyMessage();
    }

    private static MutableComponent getPlayerNameComponent(Component component, boolean originalCall) {
        List<Component> components = component.getSiblings();
        if (originalCall)
            components = components.subList(0, components.size() - 1);
        MutableComponent newComponent = Component.empty();
        newComponent.append(component.plainCopy().withStyle(component.getStyle()));
        for (Component sibling : components) {
            newComponent.append(getPlayerNameComponent(sibling, false).withStyle(sibling.getStyle()));
        }
        return newComponent;
    }

    private static Component getChatMessage(Component component) {
        return (Component)((TranslatableContents)component.getContents()).getArgs()[1];
    }

    static {
        if (ModList.get().isLoaded("appleskin"))
            IntoxicationAppleSkinCompatNeoForge.init();
    }
}