package umpaz.brewinandchewin.fabric.container;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedFluidTank;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.FluidUnit;
import umpaz.brewinandchewin.fabric.utility.AmountedFluidVariant;

public class KegFluidTankFabric extends SingleFluidStorage implements AbstractedFluidTank {
    private final long capacity;

    public KegFluidTankFabric(long capacity) {
        this.capacity = capacity;
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return getFluidCapacity();
    }

    @Override
    public long getFluidCapacity() {
        return capacity;
    }

    @Override
    public AbstractedFluidStack getAbstractedFluid() {
        return new AbstractedFluidStack(variant.getFluid(), getAmount(), variant.getComponentMap(), FluidUnit.DROPLETS, new AmountedFluidVariant(variant, getAmount(), FluidUnit.DROPLETS));
    }

    @Override
    public void setAbstractedFluid(AbstractedFluidStack stack) {
        AmountedFluidVariant unwrapped = unwrapFluid(stack);
        variant = unwrapped.variant();
        amount = unwrapped.amount();
    }

    @Override
    public AbstractedFluidStack fill(AbstractedFluidStack fluidStack, boolean simulate) {
        long newAmount = fluidStack.unit().convertToLoader(fluidStack.amount());
        try (Transaction t = TransferUtil.getTransaction()) {
            FluidVariant variant = FluidVariant.of(fluidStack.fluid(), fluidStack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY);
            long newFill = insert(variant, newAmount, t);
            if (!simulate)
                t.commit();
            return new AbstractedFluidStack(variant.getFluid(), newFill, variant.getComponentMap(), FluidUnit.DROPLETS, new AmountedFluidVariant(variant, newFill, FluidUnit.DROPLETS));
        }
    }

    @Override
    public AbstractedFluidStack drain(long maxDrain, FluidUnit unit, boolean simulate) {
        long newMax = unit.convertToLoader(maxDrain);
        try (Transaction t = TransferUtil.getTransaction()) {
            long extractedAmount = extract(variant, newMax, t);
            AbstractedFluidStack stack = new AbstractedFluidStack(variant.getFluid(), extractedAmount);
            if (!simulate)
                t.commit();
            return stack;
        }
    }

    @Override
    public boolean isEmpty() {
        return isResourceBlank();
    }

    private AmountedFluidVariant unwrapFluid(AbstractedFluidStack stack)  {
        if (stack.loaderSpecific() instanceof AmountedFluidVariant fluidVariant)
            return fluidVariant;

        if (stack.isEmpty())
            return AmountedFluidVariant.EMPTY;

        return new AmountedFluidVariant(FluidVariant.of(stack.fluid(), stack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY), stack.unit().convertToLoader(stack.amount()), FluidUnit.DROPLETS);
    }
}
