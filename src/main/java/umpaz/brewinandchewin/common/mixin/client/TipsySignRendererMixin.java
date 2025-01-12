package umpaz.brewinandchewin.common.mixin.client;

import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import umpaz.brewinandchewin.client.utility.BnCSignTextUtils;
import umpaz.brewinandchewin.common.utility.BnCTextUtils;
import vectorwing.farmersdelight.client.renderer.CanvasSignRenderer;

public class TipsySignRendererMixin {

    @Mixin(SignRenderer.class)
    public static class TipsySignRenderMixin {
        @ModifyVariable(method = "renderSignText(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/SignText;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIIZ)V", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
        private SignText brewinandchewin$renderSignText(SignText signText) {
            return BnCSignTextUtils.signRenderer(signText);
        }
    }

    @Mixin(CanvasSignRenderer.class)
    public static class TipsyCanvasSignRenderMixin {
        @ModifyVariable(method = "renderSignText(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/SignText;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIIZ)V", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
        private SignText brewinandchewin$renderSignText(SignText signText) {
            return BnCSignTextUtils.signRenderer(signText);
        }
    }
}
