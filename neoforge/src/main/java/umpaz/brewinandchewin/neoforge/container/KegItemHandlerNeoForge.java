package umpaz.brewinandchewin.neoforge.container;

import net.neoforged.neoforge.items.ItemStackHandler;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedItemHandler;

public class KegItemHandlerNeoForge extends ItemStackHandler implements AbstractedItemHandler {
    public KegItemHandlerNeoForge(int size) {
        super(size);
    }

    @Override
    public int getSlotCount() {
        return getSlots();
    }
}
