package umpaz.brewinandchewin.common.block.entity.container;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;

public interface AbstractedFluidTank {
    long getFluidCapacity();
    AbstractedFluidStack getAbstractedFluid();
    void setAbstractedFluid(AbstractedFluidStack stack);

    AbstractedFluidStack fill(AbstractedFluidStack fluidStack, boolean simulate);
    AbstractedFluidStack drain(int maxDrain, boolean simulate);

    boolean isEmpty();

    default void readFromNbt(CompoundTag tag, HolderLookup.Provider provider) {}
    default CompoundTag writeToNbt(HolderLookup.Provider provider) {
        return new CompoundTag();
    }
}
