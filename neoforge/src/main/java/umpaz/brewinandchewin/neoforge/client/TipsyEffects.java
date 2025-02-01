package umpaz.brewinandchewin.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.BnCConfiguration;
import umpaz.brewinandchewin.common.registry.BnCEffects;
import umpaz.brewinandchewin.neoforge.client.integration.IntoxicationAppleSkinCompatNeoForge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = BrewinAndChewin.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class TipsyEffects {
    @SubscribeEvent
    public static void whatsYourName(RenderNameTagEvent event) {
        if (BnCConfiguration.NAME_SCRAMBLE.get())
            if (Minecraft.getInstance().player != null) {
                if (Minecraft.getInstance().player.hasEffect(BnCEffects.TIPSY) && Minecraft.getInstance().player.getEffect(BnCEffects.TIPSY).getAmplifier() >= BnCConfiguration.LEVEL_NAME_SCRAMBLE.get()) {
                    int amplifier = Minecraft.getInstance().player.getEffect(BnCEffects.TIPSY).getAmplifier() - BnCConfiguration.LEVEL_NAME_SCRAMBLE.get();
                    StringBuilder textBuilder = new StringBuilder(event.getContent().getString());
                    RandomSource random = Minecraft.getInstance().player.getRandom();
                    int amount = (int) ((amplifier + 1) * ((textBuilder.length()) / 10f)) + random.nextInt(5);
                    for (int i = 0; i < amount; i++) {
                        // pick a random word
                        String[] words = textBuilder.toString().split(" ");
                        int wordIndex = random.nextInt(words.length);
                        String word = words[wordIndex];

                        if (word.length() < 4)
                            continue;

                        int wordStart = Arrays.asList(words).subList(0, wordIndex).stream().mapToInt(String::length).sum() + wordIndex;

                        // pick a random character in the word, excluding the first and last letters
                        int index = wordStart + random.nextInt(1, Math.max(word.length() - 2, 2));
                        // pick an index within range
                        int newIndex = Mth.clamp(index + random.nextInt(Math.max(word.length() - 2, 2)), wordStart + 1, wordStart + word.length() - 2);

                        // swap the characters
                        char temp = textBuilder.charAt(index);
                        textBuilder.setCharAt(index, textBuilder.charAt(newIndex));
                        textBuilder.setCharAt(newIndex, temp);
                    }
                    event.setContent(Component.literal(textBuilder.toString()));
                }
            }
    }

    // TODO: Ditch the Forge event so we can use the MODIFIED chat trust level.
    @SubscribeEvent
    public static void iCanHear(ClientChatReceivedEvent.Player event) {
        if (BnCConfiguration.CHAT_SCRAMBLE.get()) {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(event.getSender());
            if (player != null)
                if (player.hasEffect(BnCEffects.TIPSY) && player.getEffect(BnCEffects.TIPSY).getAmplifier() >= BnCConfiguration.LEVEL_CHAT_SCRAMBLE.get()) {
                    StringBuilder textBuilder = new StringBuilder(event.getMessage().getString());
                    int afterPlayerName = (textBuilder.indexOf("[") == 0 || textBuilder.indexOf("<") == 0) ? textBuilder.indexOf(" ") + 1 : 0;

                    int amplifier = player.getEffect(BnCEffects.TIPSY).getAmplifier() - BnCConfiguration.LEVEL_CHAT_SCRAMBLE.get();
                    int amnt = (int) ((amplifier + 1) * ((textBuilder.length() - afterPlayerName) / 10f)) + player.getRandom().nextInt(5);
                    for (int i = 0; i < amnt; i++) {
                        // pick a random word
                        List<String> words = Arrays.stream(textBuilder.toString().split(" ")).collect(Collectors.toCollection(ArrayList::new));
                        // Remove the player name from the word list.
                        if (afterPlayerName > 0)
                            words.remove(0);
                        int wordIndex = player.getRandom().nextInt(words.size());
                        String word = words.get(wordIndex);

                        if (word.length() < 4)
                            continue;

                        int wordStart = Arrays.stream(textBuilder.toString().split(" ")).toList().subList(0, wordIndex + 1).stream().mapToInt(String::length).sum() + wordIndex;

                        // pick a random character in the word, excluding the first and last letters
                        int index = wordStart + player.getRandom().nextInt(2, Math.max(word.length() - 2, 3));
                        // pick an index within range
                        int newIndex = Mth.clamp(index + player.getRandom().nextInt(Math.max(word.length() - 2, 3)), wordStart + 1, wordStart + word.length() - 2);

                        // swap the characters
                        char temp = textBuilder.charAt(index);
                        textBuilder.setCharAt(index, textBuilder.charAt(newIndex));
                        textBuilder.setCharAt(newIndex, temp);
                    }
                    event.setMessage(Component.literal(textBuilder.toString()));
                }
        }
    }

    static {
        if (ModList.get().isLoaded("appleskin"))
            IntoxicationAppleSkinCompatNeoForge.init();
    }
}