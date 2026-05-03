package umpaz.brewinandchewin.fabric.mixin.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import umpaz.brewinandchewin.client.utility.BnCClientTextUtils;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V"), index = 1)
    private Component brewinandchewin$scrambleName(Component displayName) {
        return BnCClientTextUtils.nameTagRenderer(displayName);
    }
}
