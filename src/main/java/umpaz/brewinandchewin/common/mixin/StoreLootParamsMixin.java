package umpaz.brewinandchewin.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import umpaz.brewinandchewin.common.access.LootParamsParamSetAccess;

public class StoreLootParamsMixin {
    @Mixin(LootParams.class)
    public static class LootParamsMixin implements LootParamsParamSetAccess {
        @Unique
        private LootContextParamSet brewinandchewin$paramSet;

        @Override
        public LootContextParamSet brewinandchewin$getParamSet() {
            return brewinandchewin$paramSet;
        }

        @Override
        public void brewinandchewin$setParamSet(LootContextParamSet value) {
            this.brewinandchewin$paramSet = value;
        }
    }

    @Mixin(LootParams.Builder.class)
    public static class LootParamsBuilderMixin {
        @ModifyReturnValue(method = "create", at = @At("RETURN"))
        private LootParams brewinandchewin$handleLootParams(LootParams original, LootContextParamSet paramSet) {
            ((LootParamsParamSetAccess)original).brewinandchewin$setParamSet(paramSet);
            return original;
        }
    }
}
