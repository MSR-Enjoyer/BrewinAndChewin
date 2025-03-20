

package umpaz.brewinandchewin.common.network.clientbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.utility.BnCClientTextUtils;

public record MakeNextPlayerChatTipsyClientboundPacket(int level) implements CustomPacketPayload {
    public static final ResourceLocation ID = BrewinAndChewin.asResource("make_next_player_chat_tipsy");
    public static final Type<MakeNextPlayerChatTipsyClientboundPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, MakeNextPlayerChatTipsyClientboundPacket> STREAM_CODEC = StreamCodec.of(MakeNextPlayerChatTipsyClientboundPacket::encode, MakeNextPlayerChatTipsyClientboundPacket::new);

    public MakeNextPlayerChatTipsyClientboundPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public static void encode(FriendlyByteBuf buf, MakeNextPlayerChatTipsyClientboundPacket packet) {
        buf.writeInt(packet.level());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle() {
        BnCClientTextUtils.tipsyMessageLevel = level();
    }
}
