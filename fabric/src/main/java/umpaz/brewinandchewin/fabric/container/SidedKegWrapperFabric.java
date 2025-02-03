package umpaz.brewinandchewin.fabric.container;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import org.jetbrains.annotations.Nullable;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedItemHandler;
import umpaz.brewinandchewin.common.block.entity.container.SidedKegWrapper;

public class SidedKegWrapperFabric extends SidedKegWrapper implements SlottedStackStorage {
    public SidedKegWrapperFabric(AbstractedItemHandler itemHandler, @Nullable Direction side) {
        super(itemHandler, side);
    }

    @Override
    public SingleSlotStorage<ItemVariant> getSlot(int slot) {
        return null;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }
}
