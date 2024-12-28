package umpaz.brewinandchewin.common.block.entity.container;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.mixin.ServerPlaceRecipeAccessor;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.common.utility.KegRecipeWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KegPlaceRecipe extends ServerPlaceRecipe<KegRecipeWrapper> {
    private final RecipeManager manager;

    public KegPlaceRecipe(RecipeBookMenu<KegRecipeWrapper> menu, RecipeManager manager) {
        super(menu);
        this.manager = manager;
        if (menu instanceof KegMenu kegMenu)
            ((ServerPlaceRecipeAccessor)this).brewinandchewin$setStackedContents(new KegStackedContents(kegMenu, manager));
    }

    @Override
    protected void handleRecipeClicked(Recipe<KegRecipeWrapper> recipe, boolean placeAll) {
        boolean flag = this.menu.recipeMatches(recipe);
        int biggestCraftableStack = this.stackedContents.getBiggestCraftableStack(recipe, null);
        if (flag) {
            for(int j = 0; j < this.menu.getGridHeight() * this.menu.getGridWidth() + 1; ++j) {
                if (j != this.menu.getResultSlotIndex()) {
                    ItemStack itemstack = this.menu.getSlot(j).getItem();
                    if (!itemstack.isEmpty() && Math.min(biggestCraftableStack, itemstack.getMaxStackSize()) < itemstack.getCount() + 1) {
                        return;
                    }
                }
            }
        }

        int j1 = this.getStackSize(placeAll, biggestCraftableStack, flag);
        IntList intlist = new IntArrayList();
        if (this.stackedContents.canCraft(recipe, intlist, j1)) {
            int k = j1;

            for(int l : intlist) {
                int i1 = StackedContents.fromStackingIndex(l).getMaxStackSize();
                if (i1 < k) {
                    k = i1;
                }
            }

            if (this.stackedContents.canCraft(recipe, intlist, k)) {
                if (recipe instanceof KegFermentingRecipe fermentingRecipe && menu instanceof KegMenu kegMenu) {
                    KegBlockEntity blockEntity = kegMenu.blockEntity;
                    FluidTank kegTank = kegMenu.kegTank;

                    // TODO: Fix fluid removal.
                    if (fermentingRecipe.getFluidIngredient() == null) {
                        if (!kegTank.isEmpty()) {
                            List<KegPouringRecipe> pouringRecipes = manager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream().filter(kegPouringRecipe -> kegPouringRecipe.getRawFluid().isSame(kegTank.getFluid().getRawFluid())).toList();
                            for (int i = 0; i < inventory.items.size(); ++i) {
                                if (kegTank.isEmpty())
                                    break;
                                ItemStack stack = inventory.items.get(i);
                                Optional<KegPouringRecipe> optionalData = pouringRecipes.stream().filter(pouring -> {
                                    if (pouring.isStrict())
                                        return ItemStack.isSameItemSameTags(stack, pouring.getContainer());
                                    return ItemStack.isSameItem(stack, pouring.getContainer());
                                }).findFirst();
                                if (optionalData.isPresent()) {
                                    int finalI = i;
                                    blockEntity.extractInGui(blockEntity, stack, stack.getCount()).forEach(s -> {
                                        if (!inventory.add(inventory.items.get(finalI).isEmpty() ? finalI : inventory.getSlotWithRemainingSpace(s), s))
                                            inventory.player.drop(s, false);
                                    });
                                }
                            }
                        }
                        // TODO: Fix fluid additions above the required amount.
                        // TODO: Fix not being allowed to add more fluids.
                    } else if (!kegTank.getFluid().isFluidEqual(fermentingRecipe.getFluidIngredient()) || kegTank.getFluidAmount() % fermentingRecipe.getAmount() == 0 && kegTank.getFluidAmount() < kegTank.getCapacity()) {
                        List<RecipeItem> extractItems = new ArrayList<>();

                        if (!kegTank.isEmpty() && !kegTank.getFluid().isFluidEqual(fermentingRecipe.getFluidIngredient())) {
                            List<KegPouringRecipe> pouringRecipes = manager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream().filter(kegPouringRecipe -> kegPouringRecipe.getFluid(ItemStack.EMPTY).isFluidEqual(kegTank.getFluid())).toList();
                            int fluidToExtract = kegTank.getFluidAmount();
                            for (int i = 0; i < inventory.items.size(); ++i) {
                                ItemStack stack = inventory.items.get(i);
                                Optional<KegPouringRecipe> optionalData = pouringRecipes.stream().filter(pouring -> {
                                    if (stack.isEmpty())
                                        return false;
                                    if (pouring.isStrict())
                                        return ItemStack.isSameItemSameTags(stack, pouring.getContainer());
                                    return ItemStack.isSameItem(stack, pouring.getContainer());
                                }).findFirst();
                                if (optionalData.isPresent()) {
                                    int itemAmount = Math.min(kegTank.getFluid().getAmount() / optionalData.get().getAmount(), stack.getCount());
                                    ItemStack inputStack = optionalData.get().getResultItem(blockEntity.getLevel().registryAccess());
                                    if (extractItems.stream().anyMatch(recipeItem -> recipeItem.fluidAmount > optionalData.get().getAmount()))
                                        continue;
                                    if (extractItems.stream().anyMatch(recipeItem -> recipeItem.fluidAmount < optionalData.get().getAmount())) {
                                        fluidToExtract = kegTank.getFluidAmount();
                                        extractItems.clear();
                                    }
                                    if (fluidToExtract <= 0)
                                        break;
                                    extractItems.add(new RecipeItem(i, itemAmount, optionalData.get().getAmount(), optionalData.get().getContainer().copyWithCount(itemAmount), inputStack.copyWithCount(itemAmount)));
                                    fluidToExtract -= optionalData.get().getAmount() * itemAmount;
                                }
                            }

                            if (fluidToExtract > 0)
                                return;

                            List<RecipeItem> temporaryExtracts = List.copyOf(extractItems);
                            extractItems.clear();
                            for (RecipeItem extractItem : temporaryExtracts) {
                                inventory.items.get(extractItem.slot).shrink(extractItem.maxInsert);
                                ItemStack copiedContainer = extractItem.container.copy();
                                List<ItemStack> extracted = blockEntity.extractInGui(blockEntity, extractItem.container, extractItem.maxInsert);
                                extractItems.addAll(extracted.stream().map(stack -> new RecipeItem(extractItem.slot, extractItem.maxInsert, extractItem.fluidAmount, copiedContainer, stack)).toList());
                            }
                            if (!extractItems.isEmpty())
                                intlist.removeInt(intlist.size() - 1);
                        }

                        List<KegPouringRecipe> pouringRecipes = manager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream().filter(kegPouringRecipe -> kegPouringRecipe.getFluid(ItemStack.EMPTY).isFluidEqual(fermentingRecipe.getFluidIngredient())).toList();
                        List<RecipeItem> insertItems = new ArrayList<>();
                        int fluidToInsert = 0;
                        for (int i = 0; i < inventory.items.size(); ++i) {
                            ItemStack stack = inventory.items.get(i);
                            Optional<KegPouringRecipe> optionalData = pouringRecipes.stream().filter(pouring -> {
                                if (pouring.isStrict())
                                    return ItemStack.isSameItemSameTags(stack, pouring.getOutput());
                                return ItemStack.isSameItem(stack, pouring.getOutput());
                            }).findFirst();
                            if (optionalData.isPresent()) {
                                int itemAmount = Math.min(fermentingRecipe.getFluidIngredient().getAmount() / optionalData.get().getAmount(), stack.getCount());
                                ItemStack outputStack = optionalData.get().getOutput().copyWithCount(itemAmount);
                                if (insertItems.stream().anyMatch(recipeItem -> recipeItem.fluidAmount > optionalData.get().getAmount()))
                                    continue;
                                if (insertItems.stream().anyMatch(recipeItem -> recipeItem.fluidAmount < optionalData.get().getAmount())) {
                                    fluidToInsert = 0;
                                    insertItems.clear();
                                }
                                if (fluidToInsert >= fermentingRecipe.getFluidIngredient().getAmount())
                                    break;
                                insertItems.add(new RecipeItem(i, itemAmount, optionalData.get().getAmount(), optionalData.get().getContainer().copy(), outputStack));
                                fluidToInsert += optionalData.get().getAmount() * itemAmount;
                            }
                        }
                        if (!insertItems.isEmpty()) {
                            List<RecipeItem> temporaryInserts = List.copyOf(insertItems);
                            insertItems.clear();
                            for (RecipeItem insertItem : temporaryInserts) {
                                inventory.items.get(insertItem.slot).shrink(insertItem.maxInsert);
                                ItemStack copiedOutput = insertItem.output.copy();
                                List<ItemStack> inserted = blockEntity.extractInGui(blockEntity, insertItem.output, insertItem.maxInsert);
                                insertItems.addAll(inserted.stream().map(stack -> new RecipeItem(insertItem.slot, insertItem.maxInsert, insertItem.fluidAmount, copiedOutput, stack)).toList());
                            }

                            if (!extractItems.isEmpty())
                                insertItems.addAll(extractItems);

                            insertItems.removeIf(insertItem -> insertItem.output == null || insertItem.container.isEmpty());
                            insertItems.forEach(e -> {
                                if (!inventory.add(inventory.items.get(e.slot).isEmpty() ? e.slot : inventory.getSlotWithRemainingSpace(e.output), e.output))
                                    inventory.player.drop(e.output, false);
                            });
                            intlist.removeInt(intlist.size() - 1);
                        }
                    }
                }
                this.clearGrid();
                this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), recipe, intlist.iterator(), k);
            }
        }
    }

    private record RecipeItem(int slot, int maxInsert, int fluidAmount, ItemStack container, ItemStack output) {}
}
