package umpaz.brewinandchewin.common.block.entity.container;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public interface AbstractedItemHandler {
    int getSlotCount();
    ItemStack getStackInSlot(int slot);
    void setStackInSlot(int slot, ItemStack stack);

    ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

    ItemStack extractItem(int slot, int amount, boolean simulate);

    boolean isItemValid(int slot, @Nonnull ItemStack stack);
    int getSlotLimit(int slot);

    default void readFromNbt(CompoundTag tag, HolderLookup.Provider provider) {}
    default CompoundTag writeToNbt(HolderLookup.Provider provider) {
        return new CompoundTag();
    }
}
