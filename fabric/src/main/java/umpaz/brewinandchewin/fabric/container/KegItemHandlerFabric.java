package umpaz.brewinandchewin.fabric.container;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import umpaz.brewinandchewin.common.container.AbstractedItemHandler;
import vectorwing.farmersdelight.refabricated.inventory.ItemStackHandler;

public class KegItemHandlerFabric extends ItemStackHandler implements AbstractedItemHandler {
    public KegItemHandlerFabric(int size) {
        super(size);
    }

    public void readFromNbt(CompoundTag tag, HolderLookup.Provider provider) {
        deserializeNBT(provider, tag);
    }
    public CompoundTag writeToNbt(HolderLookup.Provider provider) {
        return serializeNBT(provider);
    }
}
