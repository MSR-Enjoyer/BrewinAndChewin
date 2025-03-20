package umpaz.brewinandchewin.common.mixin.client;

import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.ServerRecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.recipebook.BnCRecipeBook;
import umpaz.brewinandchewin.common.network.clientbound.SendRecipeBookValuesClientboundPacket;

import java.util.List;

@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin extends RecipeBook {
    @Inject(method = "sendRecipes", at = @At("TAIL"))
    private void brewinandchewin$sendCookingRecipeValues(ClientboundRecipePacket.State state, ServerPlayer player, List<ResourceLocation> recipes, CallbackInfo ci) {
        BrewinAndChewin.getHelper().sendClientbound(player, new SendRecipeBookValuesClientboundPacket(getBookSettings().isOpen(BnCRecipeBook.FERMENTING.get()), getBookSettings().isFiltering(BnCRecipeBook.FERMENTING.get())));
    }
}