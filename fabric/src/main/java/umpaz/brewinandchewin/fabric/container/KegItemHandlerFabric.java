package umpaz.brewinandchewin.fabric.container;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerContainer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.world.item.ItemStack;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedItemHandler;
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
            // TODO: Handle simulated extraction.
            return ItemStack.EMPTY;
        }
        return removeItem(slot, amount);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return isItemValid(slot, ItemVariant.of(stack), stack.getCount());
    }
}
