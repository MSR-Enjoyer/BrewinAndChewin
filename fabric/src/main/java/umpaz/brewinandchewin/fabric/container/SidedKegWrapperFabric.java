package umpaz.brewinandchewin.fabric.container;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import umpaz.brewinandchewin.common.container.AbstractedItemHandler;
import umpaz.brewinandchewin.common.block.entity.container.SidedKegWrapper;
import vectorwing.farmersdelight.refabricated.inventory.ItemHandler;
import vectorwing.farmersdelight.refabricated.inventory.ItemStackHandler;
import vectorwing.farmersdelight.refabricated.inventory.ItemStackStorage;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

public class SidedKegWrapperFabric extends SidedKegWrapper implements ItemHandler {
    public SidedKegWrapperFabric(AbstractedItemHandler itemHandler, @Nullable Direction side) {
        super(itemHandler, side);
    }

    @Override
    public SingleSlotStorage<ItemVariant> getSlot(int slot) {
        return ((ItemHandler)itemHandler).getSlot(slot);
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        long inserted = 0;
        Iterator<ItemStackStorage> itr = getInsertableSlotsFor(resource);
        while (itr.hasNext()) {
            ItemStackStorage slot = itr.next();
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
        for (ItemStackStorage slot : getSlotsContaining(resource, true)) {
            extracted += slot.extract(resource, maxAmount - extracted, transaction);
            if (extracted >= maxAmount)
                break;
        }
        return extracted;
    }


    @Override
    public long insertSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (!isValidSlot(slot, false))
            return 0;
        return ((ItemStackHandler)itemHandler).extractSlot(slot, resource, maxAmount, transaction);
    }

    @Override
    public long extractSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (!isValidSlot(slot, true))
            return 0;
        return ((ItemStackHandler)itemHandler).extractSlot(slot, resource, maxAmount, transaction);
    }

    @Override
    public void commitModifiedStacks() {
        ((ItemStackHandler)itemHandler).commitModifiedStacks();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return extractItem(slot, amount, false);
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        ItemStackHandler handler = ((ItemStackHandler)itemHandler);
        return side != null && !side.equals(Direction.UP) ?
                (Iterator) handler.getSlots().subList(4, 6).iterator() :
                (Iterator) handler.getSlots().subList(0, 4).iterator();
    }

    public SortedSet<ItemStackStorage> getSlotsContaining(ItemVariant resource, boolean output) {
        ItemStackHandler handler = ((ItemStackHandler)itemHandler);
        List<SingleSlotStorage<ItemVariant>> slots = side != null && !side.equals(Direction.UP) ?
                List.of(output ? handler.getSlots().get(5) : handler.getSlots().get(4)) :
                handler.getSlots().subList(0, 4);
        return slots.stream()
                .map(storageView -> (ItemStackStorage)storageView).filter(storageView -> storageView.getResource().equals(resource))
                .collect(Collectors.toCollection(ObjectLinkedOpenHashSet::new));
    }

    public Iterator<ItemStackStorage> getInsertableSlotsFor(ItemVariant resource) {
        ItemStackHandler handler = ((ItemStackHandler)itemHandler);
        List<SingleSlotStorage<ItemVariant>> slots = side != null && !side.equals(Direction.UP) ?
                List.of(handler.getSlots().get(4)) :
                handler.getSlots().subList(0, 4);
        return slots.stream()
                .map(storageView -> (ItemStackStorage)storageView).filter(storageView -> storageView.getResource().equals(resource))
                .filter((views) -> views.isResourceBlank() || views.getResource().equals(resource))
                .iterator();
    }

    private boolean isValidSlot(int slot, boolean extract) {
        if (side == null || side.equals(Direction.UP)) {
            return slot < 3;
        } else {
            return extract ? slot == 5 : slot == 4;
        }
    }
}
