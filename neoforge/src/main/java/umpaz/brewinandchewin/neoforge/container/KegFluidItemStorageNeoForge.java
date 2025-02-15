package umpaz.brewinandchewin.neoforge.container;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import umpaz.brewinandchewin.common.container.AbstractedFluidTank;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.FluidUnit;

public class KegFluidItemStorageNeoForge implements AbstractedFluidTank {
    private final IFluidHandlerItem storage;

    public KegFluidItemStorageNeoForge(ItemStack stack) {
        storage = Capabilities.FluidHandler.ITEM.getCapability(stack, null);
    }

    @Override
    public long getFluidCapacity() {
        return storage.getTankCapacity(0);
    }

    @Override
    public long getFluidCapacity(int slot) {
        return storage.getTankCapacity(slot);
    }

    @Override
    public AbstractedFluidStack getAbstractedFluid() {
        FluidStack stack = storage.getFluidInTank(0);
        return new AbstractedFluidStack(stack.getFluid(), stack.getAmount(), stack.getComponents(), FluidUnit.MILLIBUCKETS, stack);
    }

    @Override
    public void setAbstractedFluid(AbstractedFluidStack stack) {
        storage.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
        storage.fill((FluidStack) stack.loaderSpecific(), IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public AbstractedFluidStack fill(AbstractedFluidStack stack, boolean simulate) {
        storage.fill((FluidStack)stack.loaderSpecific(), simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
        return getAbstractedFluid();
    }

    @Override
    public AbstractedFluidStack drain(int slot, long maxDrain, FluidUnit unit, boolean simulate) {
        storage.drain((int)unit.convertToLoader(maxDrain), simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
        return getAbstractedFluid();
    }

    @Override
    public ItemStack getContainer() {
        return storage.getContainer();
    }

    @Override
    public boolean isEmpty() {
        return storage.getFluidInTank(0).isEmpty();
    }

    @Override
    public boolean isFluidValid(AbstractedFluidStack stack) {
        return storage.isFluidValid(0, new FluidStack(stack.fluid().builtInRegistryHolder(), (int)stack.unit().convertToLoader(stack.amount()), stack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY));
    }
}
