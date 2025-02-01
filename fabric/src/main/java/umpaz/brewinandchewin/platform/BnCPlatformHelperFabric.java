package umpaz.brewinandchewin.platform;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedFluidTank;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedItemHandler;
import umpaz.brewinandchewin.common.block.entity.container.KegMenu;
import umpaz.brewinandchewin.common.utility.BnCMenuConstructor;
import umpaz.brewinandchewin.common.utility.AbstractedFluidIngredient;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.KegRecipeWrapper;

public class BnCPlatformHelperFabric implements BnCPlatformHelper {

    @Override
    public BnCPlatform getPlatform() {
        return BnCPlatform.FABRIC;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Component getFluidDisplayName(AbstractedFluidStack wrapper) {
        return ;
    }

    @Override
    public void openKegMenu(Player player, KegBlockEntity blockEntity, BlockPos pos) {
        player.openMenu(blockEntity);
    }

    @Override
    public MenuType<KegMenu> createMenuType(BnCMenuConstructor<KegMenu> constructor) {
        return new ExtendedScreenHandlerType<>(KegMenu::new, BlockPos.STREAM_CODEC);
    }

    @Override
    public KegRecipeWrapper createRecipeWrapper(AbstractedItemHandler itemHandler, AbstractedFluidTank fluidTank) {
        // TODO
    }

    @Override
    public KegRecipeWrapper createSidedKegWrapper(AbstractedItemHandler inventory, Direction direction) {
        return null;
    }

    @Override
    public Codec<AbstractedFluidStack> getFluidStackWrapperCodec() {
        // TODO
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AbstractedFluidStack> getFluidStackWrapperStreamCodec() {
        // TODO
    }

    @Override
    public Codec<AbstractedFluidIngredient> getFluidIngredientWrapperCodec() {
        // TODO
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AbstractedFluidIngredient> getFluidIngredientWrapperStreamCodec() {
        // TODO
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return stack.getRecipeRemainder();
    }

    @Override
    public void initFluids() {
        // TODO
    }
}
