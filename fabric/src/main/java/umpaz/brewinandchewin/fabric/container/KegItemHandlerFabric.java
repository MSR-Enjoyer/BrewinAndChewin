package umpaz.brewinandchewin.fabric.container;

import net.minecraft.world.item.ItemStack;
import umpaz.brewinandchewin.common.container.AbstractedItemHandler;
import vectorwing.farmersdelight.refabricated.inventory.ItemStackHandler;

public class KegItemHandlerFabric extends ItemStackHandler implements AbstractedItemHandler {
    public KegItemHandlerFabric(int size) {
        super(size);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return removeItem(slot, amount, simulate);
    }
}
