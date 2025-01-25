package umpaz.brewinandchewin.common.mixin.client.integration.appleskin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.Inject;
import squeek.appleskin.client.HUDOverlayHandler;

@Mixin(HUDOverlayHandler.class)
public interface HUDOverlayHandlerAccessor {
    @Invoker("shouldRenderAnyOverlays")
    static boolean brewinandchewin$shouldRenderAnyOverlays() {
        throw new RuntimeException("");
    }

    @Accessor("flashAlpha")
    static float brewinandchewin$flashAlpha() {
        throw new RuntimeException("");
    }

    @Invoker("generateHungerBarOffsets")
    static void brewinandchewin$generateHungerBarOffsets(int top, int left, int right, int ticks, Player player) {
        throw new RuntimeException("");
    }
}
