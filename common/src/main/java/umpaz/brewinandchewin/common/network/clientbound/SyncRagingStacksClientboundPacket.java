
package umpaz.brewinandchewin.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import umpaz.brewinandchewin.common.capability.RagingCapability;

import java.util.function.Supplier;

public record SyncRagingStacksClientboundPacket(int entityId, int stacks) {
    public SyncRagingStacksClientboundPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt());
    }

    public static void encode(FriendlyByteBuf buf, SyncRagingStacksClientboundPacket packet) {
        buf.writeInt(packet.entityId());
        buf.writeInt(packet.stacks());
    }

    public void handle() {
        Minecraft.getInstance().execute(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(entityId());

            if (!(entity instanceof LivingEntity living))
                return;

            living.getCapability(RagingCapability.INSTANCE).ifPresent(cap -> {
                cap.setStacks(stacks());
            });
        });
    }
}
