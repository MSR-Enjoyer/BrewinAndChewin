package umpaz.brewinandchewin.client.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SignText;
import umpaz.brewinandchewin.client.BrewinAndChewinClient;
import umpaz.brewinandchewin.common.BnCConfiguration;
import umpaz.brewinandchewin.common.registry.BnCEffects;

import java.util.*;
import java.util.stream.Collectors;

public class BnCTextUtils {
    public static int tipsyMessageLevel = 0;
    private static Component nextTipsyMessage;

    public static void setupChatMessage(Component original, UUID sender) {
        if (BnCConfiguration.CLIENT_CONFIG.get().scrambleChat() && tipsyMessageLevel > 0 ||
                Minecraft.getInstance().player.hasEffect(BnCEffects.TIPSY) && Minecraft.getInstance().player.getEffect(BnCEffects.TIPSY).getAmplifier() >= BnCConfiguration.COMMON_CONFIG.get().root().levelChatScramble()) {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(sender);
            if (player != null) {
                StringBuilder textBuilder = new StringBuilder(original.getString());
                int afterPlayerName = (textBuilder.indexOf("[") == 0 || textBuilder.indexOf("<") == 0) ? textBuilder.indexOf(" ") + 1 : 0;

                int amplifier = tipsyMessageLevel;
                if (Minecraft.getInstance().player.hasEffect(BnCEffects.TIPSY) && amplifier < Minecraft.getInstance().player.getEffect(BnCEffects.TIPSY).getAmplifier())
                    amplifier = Minecraft.getInstance().player.getEffect(BnCEffects.TIPSY).getAmplifier();
                amplifier = amplifier - BnCConfiguration.COMMON_CONFIG.get().root().levelChatScramble();
                RandomSource random = RandomSource.create(0L);
                int amnt = (int) ((amplifier + 1) * ((textBuilder.length() - afterPlayerName) / 10f)) + random.nextInt(5);
                for (int i = 0; i < amnt; i++) {
                    // pick a random word
                    List<String> words = Arrays.stream(textBuilder.toString().split(" ")).collect(Collectors.toCollection(ArrayList::new));
                    // Remove the player name from the word list.
                    if (afterPlayerName > 0)
                        words.remove(0);
                    int wordIndex = random.nextInt(words.size());
                    String word = words.get(wordIndex);

                    if (word.length() < 4)
                        continue;

                    int wordStart = Arrays.stream(textBuilder.toString().split(" ")).toList().subList(0, wordIndex + 1).stream().mapToInt(String::length).sum() + wordIndex;

                    // pick a random character in the word, excluding the first and last letters
                    int index = wordStart + random.nextInt(2, Math.max(word.length() - 2, 3));
                    // pick an index within range
                    int newIndex = Mth.clamp(index + random.nextInt(Math.max(word.length() - 2, 3)), wordStart + 1, wordStart + word.length() - 2);

                    // swap the characters
                    char temp = textBuilder.charAt(index);
                    textBuilder.setCharAt(index, textBuilder.charAt(newIndex));
                    textBuilder.setCharAt(newIndex, temp);
                }
                tipsyMessageLevel = 0;
                String text = textBuilder.toString();
                if (!original.getString().equals(text))  {
                    nextTipsyMessage = Component.literal(textBuilder.toString());
                }
            }
        }
        tipsyMessageLevel = 0;
    }

    public static Component getTipsyMessage() {
        return nextTipsyMessage;
    }

    public static void clearTipsyMessage() {
        nextTipsyMessage = null;
    }

    public static Component nameTagRenderer(Component original) {
        if (BnCConfiguration.CLIENT_CONFIG.get().scrambleName())
            if (Minecraft.getInstance().player != null) {
                if (Minecraft.getInstance().player.hasEffect(BnCEffects.TIPSY) && Minecraft.getInstance().player.getEffect(BnCEffects.TIPSY).getAmplifier() >= BnCConfiguration.COMMON_CONFIG.get().root().levelNameScramble()) {
                    int amplifier = Minecraft.getInstance().player.getEffect(BnCEffects.TIPSY).getAmplifier() - BnCConfiguration.COMMON_CONFIG.get().root().levelNameScramble();
                    StringBuilder textBuilder = new StringBuilder(original.getString());
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
                    return Component.literal(textBuilder.toString());
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
            Random random = new Random(0);
            for (int i = 0; i < 4; i++) {
                Component line = signText.getMessage(i, false);
                if (line.getString().length() <= 1) continue;
                StringBuilder text = new StringBuilder(line.getString());
                int amplifier = Minecraft.getInstance().player.getEffect(BnCEffects.TIPSY).getAmplifier() - minScrambleAmplifier;
                int amount = (int) ((amplifier + 1) * (text.length() / (10f - minScrambleAmplifier))) + random.nextInt(5);
                for (int j = 0; j < amount; j++) {
                    // pick a random word
                    String[] words = text.toString().split(" ");
                    int wordIndex = random.nextInt(words.length);
                    String word = words[wordIndex];
                    if (word.length() < 4) continue;
                    int wordStart = Arrays.asList(words).subList(0, wordIndex).stream().mapToInt(String::length).sum() + wordIndex;
                    // pick a random character in the word, excluding the first and last letters
                    int index = wordStart + random.nextInt(1, Math.max(word.length() - 2, 2));
                    // pick an index within range
                    int newIndex = Mth.clamp(index + random.nextInt(Math.max(word.length() - 2, 2)), wordStart + 1, wordStart + word.length() - 2);
                    // swap the characters
                    char temp = text.charAt(index);
                    text.setCharAt(index, text.charAt(newIndex));
                    text.setCharAt(newIndex, temp);
                }
                signText = signText.setMessage(i, Component.literal(text.toString()));
            }
        }
        return signText;
    }

}
