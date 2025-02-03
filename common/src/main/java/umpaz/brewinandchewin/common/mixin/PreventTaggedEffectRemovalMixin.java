package umpaz.brewinandchewin.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import umpaz.brewinandchewin.common.tag.BnCTags;
import vectorwing.farmersdelight.common.item.HotCocoaItem;
import vectorwing.farmersdelight.common.item.MilkBottleItem;

public class PreventTaggedEffectRemovalMixin {
    @Mixin(MilkBottleItem.class)
    public static class MilkBottle {
        @ModifyExpressionValue(method = "affectConsumer", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"), remap = false)
        private boolean brewinandchewin$preventIntoxicationRemoval(boolean original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) LivingEntity consumer, @Local MobEffectInstance effectInstance) {
            return original && (!BuiltInRegistries.MOB_EFFECT.getOrCreateTag(BnCTags.MILK_BOTTLE_LOW_PRIORITY).contains(effectInstance.getEffect()) || consumer.getActiveEffects().stream().allMatch(effect -> BuiltInRegistries.MOB_EFFECT.getOrCreateTag(BnCTags.MILK_BOTTLE_LOW_PRIORITY).contains(effect.getEffect())));
        }
    }

    @Mixin(HotCocoaItem.class)
    public static class HotCocoa {
        @ModifyExpressionValue(method = "affectConsumer", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"), remap = false)
        private boolean brewinandchewin$preventIntoxicationRemoval(boolean original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) LivingEntity consumer, @Local MobEffectInstance effectInstance) {
            return original && (!BuiltInRegistries.MOB_EFFECT.getOrCreateTag(BnCTags.HOT_COCOA_LOW_PRIORITY).contains(effectInstance.getEffect()) || consumer.getActiveEffects().stream().allMatch(effect -> BuiltInRegistries.MOB_EFFECT.getOrCreateTag(BnCTags.HOT_COCOA_LOW_PRIORITY).contains(effect.getEffect())));
        }
    }
}
