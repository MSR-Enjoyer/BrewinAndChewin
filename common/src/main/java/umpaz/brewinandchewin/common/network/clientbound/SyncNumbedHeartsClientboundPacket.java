
package umpaz.brewinandchewin.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import umpaz.brewinandchewin.common.capability.TipsyNumbedHeartsCapability;

import java.util.function.Supplier;

public record SyncNumbedHeartsClientboundPacket(int entityId, float numbedHealth, int ticksUntilDamage) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId());
        buf.writeFloat(numbedHealth());
        buf.writeInt(ticksUntilDamage());
    }

    public static SyncNumbedHeartsClientboundPacket decode(FriendlyByteBuf buf) {
        return new SyncNumbedHeartsClientboundPacket(buf.readInt(), buf.readFloat(), buf.readInt());
    }

    public void handle() {
        Minecraft.getInstance().execute(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(entityId());

            if (!(entity instanceof LivingEntity living))
                return;

            living.getCapability(TipsyNumbedHeartsCapability.INSTANCE).ifPresent(cap -> {
                cap.setNumbedHealth(packet.numbedHealth());
                cap.setTicksUntilDamage(packet.ticksUntilDamage());
            });
        });
    }
}
