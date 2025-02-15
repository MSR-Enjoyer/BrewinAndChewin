package umpaz.brewinandchewin.fabric.container;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerSlot;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import io.github.fabricators_of_create.porting_lib.util.DualSortedSetIterator;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import umpaz.brewinandchewin.common.container.AbstractedItemHandler;
import umpaz.brewinandchewin.common.block.entity.container.SidedKegWrapper;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.stream.Collectors;

public class SidedKegWrapperFabric extends SidedKegWrapper implements SlottedStackStorage {
    public SidedKegWrapperFabric(AbstractedItemHandler itemHandler, @Nullable Direction side) {
        super(itemHandler, side);
    }

    @Override
    public SingleSlotStorage<ItemVariant> getSlot(int slot) {
        return ((ItemStackHandler)itemHandler).getSlot(slot);
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        long inserted = 0;
        Iterator<ItemStackHandlerSlot> itr = getInsertableSlotsFor(resource);
        while (itr.hasNext()) {
            ItemStackHandlerSlot slot = itr.next();
            inserted += slot.insert(resource, maxAmount - inserted, transaction);
            if (inserted >= maxAmount)
                break;
        }
        return inserted;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        long extracted = 0;
        for (ItemStackHandlerSlot slot : getSlotsContaining(resource.getItem(), true)) {
            extracted += slot.extract(resource, maxAmount - extracted, transaction);
            if (extracted >= maxAmount)
                break;
        }
        return extracted;
    }


    private Iterator<ItemStackHandlerSlot> getInsertableSlotsFor(ItemVariant resource) {
        SortedSet<ItemStackHandlerSlot> slots = getSlotsContaining(resource.getItem(), false);
        SortedSet<ItemStackHandlerSlot> emptySlots = getSlotsContaining(Items.AIR, false);
        if (slots.isEmpty()) {
            return emptySlots.isEmpty() ? Collections.emptyIterator() : emptySlots.iterator();
        } else {
            return emptySlots.isEmpty() ? slots.iterator() : new DualSortedSetIterator<>(slots, emptySlots);
        }
    }

    private SortedSet<ItemStackHandlerSlot> getSlotsContaining(Item item, boolean output) {
        return ((ItemStackHandler)itemHandler).getSlotsContaining(item).stream().filter(storageViews -> isValidInputSlot(storageViews.getIndex(), output)).collect(Collectors.toCollection(() -> new ObjectAVLTreeSet<>(Comparator.comparingInt(ItemStackHandlerSlot::getIndex))));
    }

    private boolean isValidInputSlot(int slot, boolean output) {
        if (side == null || side.equals(Direction.UP)) {
            return slot < 3;
        } else {
            return output ? slot == 5 : slot == 4;
        }
    }
}
