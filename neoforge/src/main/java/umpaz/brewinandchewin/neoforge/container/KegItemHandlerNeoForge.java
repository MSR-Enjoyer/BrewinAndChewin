package umpaz.brewinandchewin.neoforge.container;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.items.ItemStackHandler;
import umpaz.brewinandchewin.common.container.AbstractedItemHandler;

public class KegItemHandlerNeoForge extends ItemStackHandler implements AbstractedItemHandler {
    public KegItemHandlerNeoForge(int size) {
        super(size);
    }

    @Override
    public int getSlotCount() {
        return getSlots();
    }

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider provider) {
        deserializeNBT(provider, tag);
    }

    @Override
    public CompoundTag writeToNbt(HolderLookup.Provider provider) {
        return serializeNBT(provider);
    }

    @Override
    public void commitModifiedStacks() {
        // No-op
    }

}
