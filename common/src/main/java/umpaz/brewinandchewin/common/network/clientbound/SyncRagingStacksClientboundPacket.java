
package umpaz.brewinandchewin.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import umpaz.brewinandchewin.common.capability.RagingCapability;

import java.util.function.Supplier;

public record SyncRagingStacksClientboundPacket(int entityId, int stacks) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId());
        buf.writeInt(stacks());
    }

    public static SyncRagingStacksClientboundPacket decode(FriendlyByteBuf buf) {
        return new SyncRagingStacksClientboundPacket(buf.readInt(), buf.readInt());
    }

    public static class Handler {
        public static void handle(SyncRagingStacksClientboundPacket packet, Supplier<NetworkEvent.Context> context) {
            if (context.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT)
                return;
            context.get().enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId());

                if (!(entity instanceof LivingEntity living))
                    return;

                living.getCapability(RagingCapability.INSTANCE).ifPresent(cap -> {
                    cap.setStacks(packet.stacks());
                });
            });
            context.get().setPacketHandled(true);
        }
    }
}
