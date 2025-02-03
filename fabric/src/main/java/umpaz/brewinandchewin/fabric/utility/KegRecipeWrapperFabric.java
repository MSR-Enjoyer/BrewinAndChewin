package umpaz.brewinandchewin.fabric.utility;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.minecraft.world.item.ItemStack;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedFluidTank;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.KegRecipeWrapper;

public class KegRecipeWrapperFabric implements KegRecipeWrapper {
    private final ItemStackHandler inventory;
    private final AbstractedFluidTank tank;

    public KegRecipeWrapperFabric(ItemStackHandler itemHandler, AbstractedFluidTank fluidHandler) {
        this.inventory = itemHandler;
        this.tank = fluidHandler;
    }

    @Override
    public AbstractedFluidStack getFluid() {
        return tank.getAbstractedFluid();
    }

    @Override
    public long getTankCapacity() {
        return tank.getFluidCapacity();
    }

    @Override
    public ItemStack getItem(int i) {
        return inventory.getStackInSlot(i);
    }

    @Override
    public int size() {
        return inventory.getSlotCount();
    }
}
