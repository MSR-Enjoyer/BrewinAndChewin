package umpaz.brewinandchewin.neoforge.platform;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedFluidTank;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedItemHandler;
import umpaz.brewinandchewin.common.block.entity.container.KegMenu;
import umpaz.brewinandchewin.common.block.entity.container.SidedKegWrapper;
import umpaz.brewinandchewin.common.utility.BnCMenuConstructor;
import umpaz.brewinandchewin.common.utility.AbstractedFluidIngredient;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.KegRecipeWrapper;
import umpaz.brewinandchewin.neoforge.container.KegItemHandlerNeoForge;
import umpaz.brewinandchewin.neoforge.container.SidedKegWrapperNeoForge;
import umpaz.brewinandchewin.neoforge.registry.BnCFluidsImpl;
import umpaz.brewinandchewin.neoforge.utility.BnCCodecs;
import umpaz.brewinandchewin.neoforge.utility.BnCStreamCodecs;
import umpaz.brewinandchewin.neoforge.utility.KegRecipeWrapperNeoForge;
import umpaz.brewinandchewin.platform.BnCPlatformHelper;
import umpaz.brewinandchewin.platform.BnCPlatform;

public class BnCPlatformHelperNeoForge implements BnCPlatformHelper {

    @Override
    public BnCPlatform getPlatform() {
        return BnCPlatform.NEOFORGE;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Component getFluidDisplayName(AbstractedFluidStack wrapper) {
        return wrapper.loaderSpecific() instanceof FluidStack fluidStack ? fluidStack.getHoverName() : Component.translatable("");
    }

    @Override
    public MenuType<KegMenu> createMenuType(BnCMenuConstructor<KegMenu> constructor) {
        return IMenuTypeExtension.create((id, inv, data) -> new KegMenu(id, inv, data.readBlockPos()));
    }

    @Override
    public AbstractedItemHandler createKegInventory(int size) {
        return new KegItemHandlerNeoForge(size);
    }

    @Override
    public KegRecipeWrapper createRecipeWrapper(AbstractedItemHandler itemHandler, AbstractedFluidTank fluidTank) {
        return new KegRecipeWrapperNeoForge((IItemHandlerModifiable) itemHandler, fluidTank);
    }

    @Override
    public SidedKegWrapper createSidedKegWrapper(AbstractedItemHandler inventory, Direction direction) {
        return new SidedKegWrapperNeoForge(inventory, direction);
    }

    @Override
    public void openKegMenu(Player player, KegBlockEntity blockEntity, BlockPos pos) {
        player.openMenu(blockEntity, pos);
    }

    @Override
    public Codec<AbstractedFluidStack> getFluidStackWrapperCodec() {
        return BnCCodecs.FLUID_STACK_WRAPPER;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AbstractedFluidStack> getFluidStackWrapperStreamCodec() {
        return BnCStreamCodecs.FLUID_STACK_WRAPPER;
    }

    @Override
    public Codec<AbstractedFluidIngredient> getFluidIngredientWrapperCodec() {
        return BnCCodecs.FLUID_INGREDIENT_WRAPPER;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AbstractedFluidIngredient> getFluidIngredientWrapperStreamCodec() {
        return BnCStreamCodecs.FLUID_INGREDIENT_WRAPPER;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return stack.getCraftingRemainingItem();
    }

    @Override
    public void initFluids() {
        BnCFluidsImpl.init();
    }
}