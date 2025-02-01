package umpaz.brewinandchewin.neoforge.container;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedItemHandler;
import umpaz.brewinandchewin.common.block.entity.container.SidedKegWrapper;

public class SidedKegWrapperNeoForge extends SidedKegWrapper implements IItemHandlerModifiable {
    public SidedKegWrapperNeoForge(AbstractedItemHandler itemHandler, @Nullable Direction side) {
        super(itemHandler, side);
    }
}
