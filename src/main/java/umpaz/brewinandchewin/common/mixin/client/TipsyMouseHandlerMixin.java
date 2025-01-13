package umpaz.brewinandchewin.common.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import umpaz.brewinandchewin.common.registry.BnCEffects;

@Mixin(MouseHandler.class)
public class TipsyMouseHandlerMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow private double accumulatedDX;

    @Shadow private double accumulatedDY;

    @ModifyExpressionValue(method = "turnPlayer()V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;smoothCamera:Z", opcode = Opcodes.GETFIELD))
    private boolean brewinandchewin$enableSmoothCamera(boolean original) {
        Player player = minecraft.player;
        if (player != null) {
            float distortionScale = minecraft.options.screenEffectScale().get().floatValue();
            if (player.hasEffect(BnCEffects.TIPSY.get()) && !player.isSpectator() && distortionScale > 0) {
                return true;
            }
        }
        return original;
    }

    @ModifyVariable(method = "turnPlayer()V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/SmoothDouble;getNewDeltaValue(DD)D", ordinal = 0), ordinal = 5)
    private double brewinandchewin$smoothCameraMovementX(double original, @Local(ordinal = 3) double d5, @Local(ordinal = 4) double d6) {
        if (minecraft.player != null && !minecraft.player.isSpectator()) {
            float distortionScale = minecraft.options.screenEffectScale().get().floatValue();
            if (minecraft.player.hasEffect(BnCEffects.TIPSY.get()) && distortionScale > 0) {
                double accumulated = (this.minecraft.options.getCameraType().isFirstPerson() && this.minecraft.player.isScoping()) ? d5 : d6;
                return Mth.lerp((1 + minecraft.player.getEffect(BnCEffects.TIPSY.get()).getAmplifier()) / 10.0 * distortionScale, accumulatedDX * accumulated, original * 1.2);
            }
        }
        return original;
    }

    @ModifyVariable(method = "turnPlayer()V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/SmoothDouble;getNewDeltaValue(DD)D", ordinal = 1), ordinal = 6)
    private double brewinandchewin$smoothCameraMovementY(double original, @Local(ordinal = 3) double d5, @Local(ordinal = 4) double d6) {
        if (minecraft.player != null && !minecraft.player.isSpectator()) {
            float distortionScale = minecraft.options.screenEffectScale().get().floatValue();
            if (minecraft.player.hasEffect(BnCEffects.TIPSY.get()) && distortionScale > 0) {
                double accumulated = (this.minecraft.options.getCameraType().isFirstPerson() && this.minecraft.player.isScoping()) ? d5 : d6;
                return Mth.lerp((1 + minecraft.player.getEffect(BnCEffects.TIPSY.get()).getAmplifier()) / 10.0 * distortionScale, accumulatedDY * accumulated, original);
            }
        }
        return original;
    }
}