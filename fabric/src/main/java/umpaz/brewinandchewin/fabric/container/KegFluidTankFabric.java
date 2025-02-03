package umpaz.brewinandchewin.fabric.container;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedFluidTank;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.fabric.utility.AmountedFluidVariant;

public class KegFluidTankFabric extends SingleFluidStorage implements AbstractedFluidTank {
    private final long capacity;

    public KegFluidTankFabric(int capacity) {
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
        return null;
    }

    @Override
    public void setAbstractedFluid(AbstractedFluidStack stack) {

    }

    @Override
    public AbstractedFluidStack fill(AbstractedFluidStack fluidStack, boolean simulate) {
        return null;
    }

    @Override
    public AbstractedFluidStack drain(int maxDrain, boolean simulate) {
        try (Transaction t = TransferUtil.getTransaction()) {
            long extractedAmount = extract(variant, maxDrain, t);
            AbstractedFluidStack stack = new AbstractedFluidStack(variant.getFluid(), extractedAmount);
            if (!simulate)
                t.commit();
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

        return new AmountedFluidVariant(FluidVariant.of(stack.fluid(), stack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY), stack.amount());
    }
}
