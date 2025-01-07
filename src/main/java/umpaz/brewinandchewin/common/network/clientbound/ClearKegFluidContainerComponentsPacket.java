
package umpaz.brewinandchewin.common.network.clientbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import umpaz.brewinandchewin.client.gui.KegScreen;

import java.util.function.Supplier;

public record ClearKegFluidContainerComponentsPacket() {
    public void encode(FriendlyByteBuf buf) {
    }

    public static ClearKegFluidContainerComponentsPacket decode(FriendlyByteBuf buf) {
        return new ClearKegFluidContainerComponentsPacket();
    }

    public static class Handler {
        public static void handle(ClearKegFluidContainerComponentsPacket packet, Supplier<NetworkEvent.Context> context) {
            if (context.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT)
                return;
            context.get().enqueueWork(KegScreen::clearFluidContainerComponents);
            context.get().setPacketHandled(true);
        }
    }
}
