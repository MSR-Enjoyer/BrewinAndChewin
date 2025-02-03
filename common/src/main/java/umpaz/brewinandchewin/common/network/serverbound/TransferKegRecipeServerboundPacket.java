package umpaz.brewinandchewin.common.network.serverbound;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.utility.BnCStreamCodecs;
import umpaz.brewinandchewin.integration.jei.transfer.FermentingTransfer;
import umpaz.brewinandchewin.integration.jei.transfer.FermentingTransferServer;

import java.util.List;

public record TransferKegRecipeServerboundPacket(ResourceLocation recipeId,
                                                 List<Pair<Integer, Integer>> resultSlots,
                                                 List<Pair<Integer, Long>> fluidSlots,
                                                 List<Pair<Integer, Long>> emptyingSlots,
                                                 List<Integer> craftingSlots,
                                                 List<Integer> inventorySlots,
                                                 boolean maxTransfer) implements CustomPacketPayload {
    public static final ResourceLocation ID = BrewinAndChewin.asResource("transfer_keg_recipe");
    public static final CustomPacketPayload.Type<TransferKegRecipeServerboundPacket> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, TransferKegRecipeServerboundPacket> STREAM_CODEC = StreamCodec.of(TransferKegRecipeServerboundPacket::encode, TransferKegRecipeServerboundPacket::new);

    public TransferKegRecipeServerboundPacket(RegistryFriendlyByteBuf buf) {
        this(
                buf.readResourceLocation(),
                BnCStreamCodecs.INT_PAIR_LIST.decode(buf),
                BnCStreamCodecs.INT_LONG_PAIR_LIST.decode(buf),
                BnCStreamCodecs.INT_LONG_PAIR_LIST.decode(buf),
                ByteBufCodecs.INT.apply(ByteBufCodecs.list()).decode(buf),
                ByteBufCodecs.INT.apply(ByteBufCodecs.list()).decode(buf),
                buf.readBoolean()
        );
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(recipeId);
        BnCStreamCodecs.INT_PAIR_LIST.encode(buf, resultSlots);
        BnCStreamCodecs.INT_LONG_PAIR_LIST.encode(buf, fluidSlots);
        BnCStreamCodecs.INT_LONG_PAIR_LIST.encode(buf, emptyingSlots);
        ByteBufCodecs.INT.apply(ByteBufCodecs.list()).encode(buf, craftingSlots);
        ByteBufCodecs.INT.apply(ByteBufCodecs.list()).encode(buf, inventorySlots);
        buf.writeBoolean(maxTransfer);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(MinecraftServer server, ServerPlayer sender) {
        server.execute(() -> {
            if (sender == null)
                return;
            var recipe = sender.getServer().getRecipeManager().byKey(recipeId());
            if (recipe.isEmpty() || !(recipe.get().value() instanceof KegFermentingRecipe kegFermentingRecipe))
                return;
            FermentingTransferServer.setItems(
                    sender,
                    kegFermentingRecipe,
                    FermentingTransfer.TransferOperations.readFromIntegers(resultSlots(), fluidSlots(), emptyingSlots(), sender.containerMenu),
                    craftingSlots().stream().map(sender.containerMenu::getSlot).toList(),
                    inventorySlots().stream().map(sender.containerMenu::getSlot).toList(),
                    maxTransfer()
            );
        });
    }
}
