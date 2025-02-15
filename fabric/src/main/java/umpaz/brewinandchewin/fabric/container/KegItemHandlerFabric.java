package umpaz.brewinandchewin.fabric.container;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerContainer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import umpaz.brewinandchewin.common.container.AbstractedItemHandler;
import vectorwing.farmersdelight.common.utility.ItemUtils;

public class KegItemHandlerFabric extends ItemStackHandlerContainer implements AbstractedItemHandler {
    public KegItemHandlerFabric(int size) {
        super(size);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return ItemUtils.insertItem(this, slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (simulate) {
            try (Transaction t = TransferUtil.getTransaction()) {
                ItemVariant variant = getVariantInSlot(slot);
                long extractedAmount = extract(variant, amount, t);

                ItemStack stack = getItem(slot);
                stack.shrink((int)extractedAmount);
                return stack;
            }
        }
        return removeItem(slot, amount);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return isItemValid(slot, ItemVariant.of(stack), stack.getCount());
    }

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider provider) {
        deserializeNBT(provider, tag);
    }

    @Override
    public CompoundTag writeToNbt(HolderLookup.Provider provider) {
        return serializeNBT(provider);
    }
}
