package umpaz.brewinandchewin.neoforge.utility;

import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedFluidTank;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.KegRecipeWrapper;

public class KegRecipeWrapperNeoForge extends RecipeWrapper implements KegRecipeWrapper {
    private final AbstractedFluidTank tank;

    public KegRecipeWrapperNeoForge(IItemHandlerModifiable itemHandler, AbstractedFluidTank fluidHandler) {
        super(itemHandler);
        this.tank = fluidHandler;
    }

    public AbstractedFluidStack getFluid() {
        return tank.getAbstractedFluid();
    }

    public int getTankCapacity() {
        return tank.getCapacity();
    }

    @Override
    public boolean isEmpty() {
        if (!tank.getAbstractedFluid().isEmpty())
            return false;
        return super.isEmpty();
    }
}
