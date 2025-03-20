package umpaz.brewinandchewin.common.utility;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.BnCConfiguration;
import umpaz.brewinandchewin.common.registry.BnCEffects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BnCTextUtils {
    public static PlayerChatMessage setupChatMessageServer(PlayerChatMessage message, ServerPlayer sender) {
        if (sender.hasEffect(BnCEffects.TIPSY) && sender.getEffect(BnCEffects.TIPSY).getAmplifier() >= BnCConfiguration.COMMON_CONFIG.get().root().levelChatScramble()) {
            StringBuilder textBuilder = new StringBuilder(message.signedContent());

            int amplifier = sender.getEffect(BnCEffects.TIPSY).getAmplifier();
            amplifier = amplifier - BnCConfiguration.COMMON_CONFIG.get().root().levelChatScramble();
            RandomSource random = RandomSource.create(0L);
            int amnt = (int) ((amplifier + 1) * (textBuilder.length() / 10f)) + random.nextInt(5);
            for (int i = 0; i < amnt; i++) {
                // pick a random word
                List<String> words = Arrays.stream(textBuilder.toString().split(" ")).collect(Collectors.toCollection(ArrayList::new));
                if (words.isEmpty())
                    continue;
                int wordIndex = random.nextInt(words.size());
                String word = words.get(wordIndex);

                if (word.length() < 4)
                    continue;

                int wordStart = Arrays.stream(textBuilder.toString().split(" ")).toList().subList(0, wordIndex).stream().mapToInt(String::length).sum() + wordIndex;

                // pick a random character in the word, excluding the first and last letters
                int index = wordStart + random.nextInt(2, Math.max(word.length() - 2, 3));
                // pick an index within range
                int newIndex = Mth.clamp(index + random.nextInt(Math.max(word.length() - 2, 3)), wordStart + 1, wordStart + word.length() - 2);

                // swap the characters
                char temp = textBuilder.charAt(index);
                textBuilder.setCharAt(index, textBuilder.charAt(newIndex));
                textBuilder.setCharAt(newIndex, temp);
            }
            String text = textBuilder.toString();
            if (!message.signedContent().equals(text))  {
                return message.withUnsignedContent(Component.literal(text));
            }
        }
        return message;
    }

    public static MutableComponent getTranslation(String key, Object... args) {
        return Component.translatable(BrewinAndChewin.MODID + "." + key, args);
    }
}
