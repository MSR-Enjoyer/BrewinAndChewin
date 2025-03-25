package umpaz.brewinandchewin.client.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SignText;
import umpaz.brewinandchewin.common.BnCConfiguration;
import umpaz.brewinandchewin.common.registry.BnCEffects;

import java.util.*;
import java.util.stream.Collectors;

public class BnCClientTextUtils {
    public static int tipsyMessageLevel = 0;
    public static long randomSeed = 0L;
    private static PlayerChatMessage nextTipsyMessage;

    public static Component modifyComponents(Component original, long randomSeed) {
        StringBuilder textBuilder = new StringBuilder(original.getContents().visit(Optional::ofNullable).orElse(""));

        if (!textBuilder.isEmpty()) {
            int amplifier = tipsyMessageLevel;
            if (Minecraft.getInstance().player.hasEffect(BnCEffects.TIPSY) && amplifier < Minecraft.getInstance().player.getEffect(BnCEffects.TIPSY).getAmplifier())
                amplifier = Minecraft.getInstance().player.getEffect(BnCEffects.TIPSY).getAmplifier();
            amplifier = amplifier - BnCConfiguration.COMMON_CONFIG.get().root().levelChatScramble();
            RandomSource random = RandomSource.create(randomSeed);
            int amnt = (int) ((amplifier + 1) * (textBuilder.length() / 6f)) + random.nextInt(amplifier, amplifier + 2) - 1;

            for (int i = 0; i < amnt; i++) {
                List<String> globalWords = Arrays.stream(textBuilder.toString().split(" ")).collect(Collectors.toCollection(ArrayList::new));
                List<String> validWords = globalWords.stream().filter(s -> s.length() > 3).collect(Collectors.toCollection(ArrayList::new));
                if (validWords.isEmpty())
                    break;
                // pick a random word
                int wordIndex = random.nextInt(validWords.size());
                String word = validWords.get(wordIndex);
                int globalWordLength = globalWords.subList(0, globalWords.indexOf(word)).stream().mapToInt(value -> value.length() + 1).sum();
                // pick a random character in the word, excluding the first and last letters
                int index = globalWordLength + random.nextInt(1, word.length() - 2);
                // pick an index within range
                int newIndex = Mth.clamp(index + (random.nextBoolean() ? 1 : -1), globalWordLength + 1,
                        globalWordLength + word.length() - 2);
                // swap the characters
                char temp = textBuilder.charAt(index);
                textBuilder.setCharAt(index, textBuilder.charAt(newIndex));
                textBuilder.setCharAt(newIndex, temp);
            }
            String text = textBuilder.toString();
            MutableComponent component = Component.literal(text).withStyle(original.getStyle());
            for (Component sibling : original.getSiblings()) {
                component.append(modifyComponents(sibling, randomSeed));
            }
            return component;
        }
        MutableComponent component = Component.empty();
        for (Component sibling : original.getSiblings()) {
            component.append(modifyComponents(sibling, randomSeed));
        }
        return component;
    }

    public static void setupChatMessage(PlayerChatMessage chatMessage) {
        if (BnCConfiguration.CLIENT_CONFIG.get().scrambleChat() && (tipsyMessageLevel > 0 ||
                Minecraft.getInstance().player.hasEffect(BnCEffects.TIPSY) && Minecraft.getInstance().player.getEffect(BnCEffects.TIPSY).getAmplifier() >= BnCConfiguration.COMMON_CONFIG.get().root().levelChatScramble())) {

            Component newMessage = modifyComponents(chatMessage.decoratedContent(), randomSeed);

            if (!chatMessage.decoratedContent().equals(newMessage))  {
                nextTipsyMessage = chatMessage.withUnsignedContent(newMessage);
            }
        }
    }

    public static PlayerChatMessage getTipsyMessage() {
        return nextTipsyMessage;
    }

    public static void clearTipsyMessage() {
        nextTipsyMessage = null;
    }

    public static Component nameTagRenderer(Component original) {
        if (BnCConfiguration.CLIENT_CONFIG.get().scrambleName())
            if (Minecraft.getInstance().player != null) {
                if (Minecraft.getInstance().player.hasEffect(BnCEffects.TIPSY) && Minecraft.getInstance().player.getEffect(BnCEffects.TIPSY).getAmplifier() >= BnCConfiguration.COMMON_CONFIG.get().root().levelNameScramble()) {
                    return modifyComponents(original, 0L);
                }
            }
        return original;
    }

    public static SignText signRenderer(SignText signText) {
        Player player = Minecraft.getInstance().player;
        if (!BnCConfiguration.CLIENT_CONFIG.get().scrambleSign() || player == null) {
            return signText;
        }
        int minScrambleAmplifier = BnCConfiguration.COMMON_CONFIG.get().root().levelSignScramble();

        if (player.hasEffect(BnCEffects.TIPSY) && player.getEffect(BnCEffects.TIPSY).getAmplifier() >= minScrambleAmplifier) {
            for (int i = 0; i < 4; i++) {
                signText = signText.setMessage(i, modifyComponents(signText.getMessage(i, false), 0L));
            }
        }
        return signText;
    }

}
