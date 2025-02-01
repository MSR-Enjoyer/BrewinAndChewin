package umpaz.brewinandchewin.neoforge.container;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedFluidTank;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;

public class KegFluidTank extends FluidTank implements AbstractedFluidTank {
    public KegFluidTank(int capacity) {
        super(capacity);
    }

    @Override
    public AbstractedFluidStack getAbstractedFluid() {
        return new AbstractedFluidStack(fluid.getFluid(), fluid.getAmount(), fluid.getComponents(), fluid);
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

        return new FluidStack(stack.fluid().builtInRegistryHolder(), stack.amount(), stack.components().asPatch());
    }
}
