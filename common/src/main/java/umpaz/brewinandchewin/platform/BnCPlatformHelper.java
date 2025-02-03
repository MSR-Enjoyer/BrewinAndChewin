package umpaz.brewinandchewin.platform;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedFluidTank;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedItemHandler;
import umpaz.brewinandchewin.common.block.entity.container.KegMenu;
import umpaz.brewinandchewin.common.block.entity.container.KegStackedContents;
import umpaz.brewinandchewin.common.block.entity.container.SidedKegWrapper;
import umpaz.brewinandchewin.common.utility.BnCMenuConstructor;
import umpaz.brewinandchewin.common.utility.AbstractedFluidIngredient;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.KegRecipeWrapper;

import java.util.List;
import java.util.function.Consumer;

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

    AbstractedItemHandler createKegInventory(int size, Consumer<Integer> onContentsChanged);

    AbstractedFluidTank createKegTank(int capacity, Runnable onContentsChanged);

    default Slot createKegSlot(AbstractedItemHandler inventory, int slot, int x, int y) {
        return createKegSlot(inventory, slot, x, y, true, null);
    }
    default Slot createKegContainerSlot(AbstractedItemHandler inventory, int slot, int x, int y) {
        return createKegSlot(inventory, slot, x, y, true, Pair.of(TextureAtlas.LOCATION_BLOCKS, KegMenu.EMPTY_CONTAINER_SLOT_TANKARD));
    }
    default Slot createKegResultSlot(AbstractedItemHandler inventory, int slot, int x, int y) {
        return createKegSlot(inventory, slot, x, y, false, null);
    }
    Slot createKegSlot(AbstractedItemHandler inventory, int slot, int x, int y, boolean canInsert, @Nullable Pair<ResourceLocation, ResourceLocation> noItemIcon);

    Ingredient createStrictFillPickerIngredient(List<KegStackedContents.PouringEntry> fluidOutputStacks);

    KegRecipeWrapper createRecipeWrapper(AbstractedItemHandler itemHandler, AbstractedFluidTank fluidTank);

    SidedKegWrapper createSidedKegWrapper(AbstractedItemHandler inventory, Direction direction);

    Codec<AbstractedFluidStack> getFluidStackWrapperCodec();

    StreamCodec<RegistryFriendlyByteBuf, AbstractedFluidStack> getFluidStackWrapperStreamCodec();

    Codec<AbstractedFluidIngredient> getFluidIngredientWrapperCodec();

    StreamCodec<RegistryFriendlyByteBuf, AbstractedFluidIngredient> getFluidIngredientWrapperStreamCodec();

    ItemStack getCraftingRemainingItem(ItemStack stack);

    void initFluids();

    void initCreativeTab();

    boolean isEdible(ItemStack stack, LivingEntity entity);

    FoodProperties getFoodProperties(ItemStack stack, LivingEntity entity);
}