package umpaz.brewinandchewin.neoforge.container;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedFluidTank;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;

public class KegFluidTankNeoForge extends FluidTank implements AbstractedFluidTank {
    public KegFluidTankNeoForge(int capacity) {
        super(capacity);
    }

    @Override
    public long getFluidCapacity() {
        return getCapacity();
    }

    @Override
    public AbstractedFluidStack getAbstractedFluid() {
        return new AbstractedFluidStack(fluid.getFluid(), fluid.getAmount(), fluid.getComponents(), fluid);
    }

    @Override
    public void setAbstractedFluid(AbstractedFluidStack stack) {
        setFluid(unwrapFluid(stack));
    }

    @Override
    public AbstractedFluidStack fill(AbstractedFluidStack fluidStack, boolean simulate) {
        fill(unwrapFluid(fluidStack), simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
        return getAbstractedFluid();
    }

    @Override
    public AbstractedFluidStack drain(int maxDrain, boolean simulate) {
        FluidStack fluid = drain(maxDrain, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
        return new AbstractedFluidStack(fluid.getFluid(), fluid.getAmount(), fluid.getComponents(), fluid);
    }

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider provider) {
        readFromNBT(provider, tag);
    }

    @Override
    public CompoundTag writeToNbt(HolderLookup.Provider provider) {
        return writeToNBT(provider, new CompoundTag());
    }

    private FluidStack unwrapFluid(AbstractedFluidStack stack)  {
        if (stack.loaderSpecific() instanceof FluidStack fluidStack)
            return fluidStack;

        if (stack.isEmpty())
            return FluidStack.EMPTY;

        return new FluidStack(stack.fluid().builtInRegistryHolder(), stack.amount(), stack.components() instanceof PatchedDataComponentMap  patched ? patched.asPatch() : DataComponentPatch.EMPTY);
    }
}
