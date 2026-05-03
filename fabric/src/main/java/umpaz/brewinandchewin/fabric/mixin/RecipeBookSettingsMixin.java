package umpaz.brewinandchewin.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.util.Pair;
import net.minecraft.stats.RecipeBookSettings;
import net.minecraft.world.inventory.RecipeBookType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import umpaz.brewinandchewin.client.recipebook.BnCRecipeBookCategories;
import umpaz.brewinandchewin.common.BnCRecipeBookTypes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Mixin(RecipeBookSettings.class)
public class RecipeBookSettingsMixin {
    @Final
    @Mutable
    @Shadow
    private static Map<RecipeBookType, Pair<String, String>> TAG_FIELDS;

    @Shadow @Final private Map<RecipeBookType, RecipeBookSettings.TypeSettings> states;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void brewinandchewin$modifyTagFields(CallbackInfo ci) {
        Map<RecipeBookType, Pair<String, String>> newMap = new HashMap<>(TAG_FIELDS);
        newMap.put(BnCRecipeBookTypes.FERMENTING, Pair.of("isBrewinAndChewinFermentingGuiOpen", "isBrewinAndChewinFermentingFilteringCraftable"));
        TAG_FIELDS = Map.copyOf(newMap);
    }

    @Inject(method = "<init>(Ljava/util/Map;)V", at = @At("TAIL"))
    private void brewinandchewin$defaultCookingRecipeBookTypeStates(CallbackInfo ci) {
        if (!states.containsKey(BnCRecipeBookTypes.FERMENTING))
            states.put(BnCRecipeBookTypes.FERMENTING, new RecipeBookSettings.TypeSettings(false, false));
    }

    @ModifyExpressionValue(method = "read(Lnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/stats/RecipeBookSettings;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/RecipeBookType;values()[Lnet/minecraft/world/inventory/RecipeBookType;"))
    private static RecipeBookType[] brewinandchewin$modifyReadFDRecipeBookSettingsToVanilla(RecipeBookType[] original) {
        return Arrays.stream(original).filter(recipeBookType -> recipeBookType != BnCRecipeBookTypes.FERMENTING).toArray(RecipeBookType[]::new);
    }

    @ModifyExpressionValue(method = "write(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/RecipeBookType;values()[Lnet/minecraft/world/inventory/RecipeBookType;"))
    private RecipeBookType[] brewinandchewin$modifyWrittenFDRecipeBookSettingsToVanilla(RecipeBookType[] original) {
        return Arrays.stream(original).filter(recipeBookType -> recipeBookType != BnCRecipeBookTypes.FERMENTING).toArray(RecipeBookType[]::new);
    }
}
