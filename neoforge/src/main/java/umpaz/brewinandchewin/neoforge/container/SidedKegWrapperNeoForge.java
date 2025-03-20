package umpaz.brewinandchewin.neoforge.container;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;
import umpaz.brewinandchewin.common.container.AbstractedItemHandler;
import umpaz.brewinandchewin.common.block.entity.container.SidedKegWrapper;

public class SidedKegWrapperNeoForge extends SidedKegWrapper implements IItemHandlerModifiable {
    public SidedKegWrapperNeoForge(AbstractedItemHandler itemHandler, @Nullable Direction side) {
        super(itemHandler, side);
    }

    @Override
    public int getSlots() {
        return getSlotCount();
    }

    @Override
    public void commitModifiedStacks() {
        // No-op
    }
}
