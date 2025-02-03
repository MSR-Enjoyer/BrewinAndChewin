package umpaz.brewinandchewin.neoforge.platform;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;
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
import umpaz.brewinandchewin.neoforge.container.KegFluidTank;
import umpaz.brewinandchewin.neoforge.container.KegItemHandlerNeoForge;
import umpaz.brewinandchewin.neoforge.container.SidedKegWrapperNeoForge;
import umpaz.brewinandchewin.neoforge.registry.BnCFluidsImpl;
import umpaz.brewinandchewin.neoforge.utility.BnCCodecs;
import umpaz.brewinandchewin.neoforge.utility.BnCStreamCodecs;
import umpaz.brewinandchewin.neoforge.utility.KegRecipeWrapperNeoForge;
import umpaz.brewinandchewin.platform.BnCPlatformHelper;
import umpaz.brewinandchewin.platform.BnCPlatform;

import java.util.List;
import java.util.function.Consumer;

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
    public AbstractedItemHandler createKegInventory(int size, Consumer<Integer> onContentsChanged) {
        return new KegItemHandlerNeoForge(size) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                onContentsChanged.accept(slot);
            }
        };
    }

    @Override
    public AbstractedFluidTank createKegTank(int capacity, Runnable onContentsChanged) {
        return new KegFluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                onContentsChanged.run();
            }
        };
    }

    @Override
    public Slot createKegSlot(AbstractedItemHandler inventory, int slot, int x, int y, boolean canInsert, @Nullable Pair<ResourceLocation, ResourceLocation> noItemIcon) {
        return new SlotItemHandler((IItemHandler)inventory, slot, x, y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return canInsert && super.mayPlace(stack);
            }

            @Override
            public @Nullable Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return noItemIcon;
            }
        };
    }

    @Override
    public Ingredient createStrictFillPickerIngredient(List<KegStackedContents.PouringEntry> fluidOutputStacks) {
        return CompoundIngredient.of(fluidOutputStacks.stream().map(p -> {
            if (p.strict())
                return DataComponentIngredient.of(true, p.stack());
            return Ingredient.of(p.stack().getItem());
        }).toArray(Ingredient[]::new));
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