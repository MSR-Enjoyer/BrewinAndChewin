package umpaz.brewinandchewin.neoforge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.util.Mth;
import net.minecraft.util.SmoothDouble;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import umpaz.brewinandchewin.common.registry.BnCEffects;

@Mixin(MouseHandler.class)
public class TipsyMouseHandlerMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private final SmoothDouble brewinandchewin$smoothTurnX = new SmoothDouble();

    @Unique
    private final SmoothDouble brewinandchewin$smoothTurnY = new SmoothDouble();

    @Inject(method = "handleAccumulatedMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;lastHandleMovementTime:D", ordinal = 1))
    private void brewinandchewin$resetSmoothTurn(CallbackInfo ci) {
        if (minecraft.player != null && minecraft.player.hasContainerOpen() || minecraft.options.smoothCamera) {
            brewinandchewin$smoothTurnX.reset();
            brewinandchewin$smoothTurnY.reset();
        }
    }

    @ModifyVariable(method = "turnPlayer", at = @At(value = "STORE", ordinal = 2), ordinal = 4) // Targets the else block if where the player is scoping.
    private double brewinandchewin$smoothCameraMovementScopedX(double original, @Local(ordinal = 1) double d1, @Local(ordinal = 3) double d5) {
        if (minecraft.player != null && !minecraft.player.isSpectator()) {
            if (minecraft.player.hasEffect(BnCEffects.TIPSY) && minecraft.player.getEffect(BnCEffects.TIPSY).getAmplifier() > 1) {
                double tipsyDelta = Math.min(1 + minecraft.player.getEffect(BnCEffects.TIPSY).getAmplifier(), 10) / 10.0;
                return brewinandchewin$smoothTurnX.getNewDeltaValue(original, Mth.lerp(tipsyDelta, d1 * 10, d1) * d5 * Math.max(1.0, tipsyDelta * 2.0));
            }
        }
        brewinandchewin$smoothTurnX.reset();
        return original;
    }

    @ModifyVariable(method = "turnPlayer", at = @At(value = "STORE", ordinal = 2 ), ordinal = 5) // Targets the else block if where the player is scoping.
    private double brewinandchewin$smoothCameraMovementScopedY(double original, @Local(ordinal = 1) double d1, @Local(ordinal = 3) double d5) {
        if (minecraft.player != null && !minecraft.player.isSpectator()) {
            if (minecraft.player.hasEffect(BnCEffects.TIPSY) && minecraft.player.getEffect(BnCEffects.TIPSY).getAmplifier() > 1) {
                double tipsyDelta = Math.min(1 + minecraft.player.getEffect(BnCEffects.TIPSY).getAmplifier(), 10) / 10.0;
                return brewinandchewin$smoothTurnY.getNewDeltaValue(original, Mth.lerp(tipsyDelta, d1 * 10, d1) * d5 * Math.max(1.0, tipsyDelta * 2.0));
            }
        }
        brewinandchewin$smoothTurnY.reset();
        return original;
    }

    @ModifyVariable(method = "turnPlayer", at = @At(value = "STORE", ordinal = 3), ordinal = 4) // Targets the else block, if smooth camera is off and the player is not scoping.
    private double brewinandchewin$smoothCameraMovementX(double original, @Local(ordinal = 1) double d1, @Local(ordinal = 4) double d6) {
        if (minecraft.player != null && !minecraft.player.isSpectator()) {
            if (minecraft.player.hasEffect(BnCEffects.TIPSY) && minecraft.player.getEffect(BnCEffects.TIPSY).getAmplifier() > 1) {
                double tipsyDelta = Math.min(1 + minecraft.player.getEffect(BnCEffects.TIPSY).getAmplifier(), 10) / 10.0;
                return brewinandchewin$smoothTurnX.getNewDeltaValue(original, Mth.lerp(tipsyDelta, d1 * 10, d1) * d6 * Math.max(1.0, tipsyDelta * 2.0));
            }
        }
        brewinandchewin$smoothTurnX.reset();
        return original;
    }

    @ModifyVariable(method = "turnPlayer", at = @At(value = "STORE", ordinal = 3), ordinal = 5) // Targets the else block, if smooth camera is off and the player is not scoping.
    private double brewinandchewin$smoothCameraMovementY(double original, @Local(ordinal = 1) double d1, @Local(ordinal = 4) double d6) {
        if (minecraft.player != null && !minecraft.player.isSpectator()) {
            if (minecraft.player.hasEffect(BnCEffects.TIPSY) && minecraft.player.getEffect(BnCEffects.TIPSY).getAmplifier() > 1) {
                double tipsyDelta = Math.min(1 + minecraft.player.getEffect(BnCEffects.TIPSY).getAmplifier(), 10) / 10.0;
                return brewinandchewin$smoothTurnY.getNewDeltaValue(original, Mth.lerp(tipsyDelta, d1 * 10, d1) * d6 * Math.max(1.0, tipsyDelta * 2.0));
            }
        }
        brewinandchewin$smoothTurnY.reset();
        return original;
    }
}