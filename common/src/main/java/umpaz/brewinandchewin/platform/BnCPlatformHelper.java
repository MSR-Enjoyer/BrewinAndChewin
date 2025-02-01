package umpaz.brewinandchewin.platform;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedFluidTank;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedItemHandler;
import umpaz.brewinandchewin.common.block.entity.container.KegMenu;
import umpaz.brewinandchewin.common.block.entity.container.SidedKegWrapper;
import umpaz.brewinandchewin.common.utility.BnCMenuConstructor;
import umpaz.brewinandchewin.common.utility.AbstractedFluidIngredient;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.KegRecipeWrapper;

public interface BnCPlatformHelper {

    BnCPlatform getPlatform();

    boolean isModLoaded(String modId);

    default boolean isModLoadedEarly(String modId) {
        return isModLoaded(modId);
    }

    boolean isDevelopmentEnvironment();

    Component getFluidDisplayName(AbstractedFluidStack wrapper);

    void openKegMenu(Player player, KegBlockEntity blockEntity, BlockPos pos);

    MenuType<KegMenu> createMenuType(BnCMenuConstructor<KegMenu> constructor);

    AbstractedItemHandler createKegInventory(int size);

    KegRecipeWrapper createRecipeWrapper(AbstractedItemHandler itemHandler, AbstractedFluidTank fluidTank);

    SidedKegWrapper createSidedKegWrapper(AbstractedItemHandler inventory, Direction direction);

    Codec<AbstractedFluidStack> getFluidStackWrapperCodec();

    StreamCodec<RegistryFriendlyByteBuf, AbstractedFluidStack> getFluidStackWrapperStreamCodec();

    Codec<AbstractedFluidIngredient> getFluidIngredientWrapperCodec();

    StreamCodec<RegistryFriendlyByteBuf, AbstractedFluidIngredient> getFluidIngredientWrapperStreamCodec();

    ItemStack getCraftingRemainingItem(ItemStack stack);

    void initFluids();
}