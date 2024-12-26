package umpaz.brewinandchewin.common.block.entity.container;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.mixin.ServerPlaceRecipeAccessor;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.common.utility.KegRecipeWrapper;

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
                    if (fermentingRecipe.getFluidIngredient() == null) {
                        if (!kegMenu.kegTank.isEmpty()) {
                            List<KegPouringRecipe> pouringRecipes = manager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream().filter(kegPouringRecipe -> kegPouringRecipe.getRawFluid().isSame(kegMenu.kegTank.getFluid().getRawFluid())).toList();
                            for (int i = 0; i < inventory.items.size(); ++i) {
                                if (kegMenu.kegTank.isEmpty())
                                    break;
                                ItemStack stack = inventory.items.get(i);
                                Optional<KegPouringRecipe> optionalData = pouringRecipes.stream().filter(pouring -> {
                                    if (pouring.isStrict())
                                        return ItemStack.isSameItemSameTags(stack, pouring.getContainer());
                                    return ItemStack.isSameItem(stack, pouring.getContainer());
                                }).findFirst();
                                if (optionalData.isPresent()) {
                                    ItemStack extracted = kegMenu.blockEntity.extractInGui(kegMenu.blockEntity, stack, ItemStack.EMPTY, stack.getCount());
                                    if (!inventory.add(i, extracted))
                                        inventory.player.drop(extracted, false);
                                }
                            }
                        }
                        if (fermentingRecipe.getFluidIngredient() != null)
                            intlist.removeInt(intlist.size() - 1);
                    } else if (kegMenu.kegTank.isEmpty() || !kegMenu.kegTank.getFluid().isFluidEqual(fermentingRecipe.getFluidIngredient())) {
                        List<KegPouringRecipe> pouringRecipes = manager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream().filter(kegPouringRecipe -> kegPouringRecipe.getFluid(ItemStack.EMPTY).isFluidEqual(fermentingRecipe.getFluidIngredient())).toList();
                        for (int i = 0; i < inventory.items.size(); ++i) {
                            if (kegMenu.kegTank.getFluidAmount() >= kegMenu.kegTank.getCapacity())
                                break;
                            ItemStack stack = inventory.items.get(i);
                            Optional<KegPouringRecipe> optionalData = pouringRecipes.stream().filter(pouring -> {
                                if (pouring.isStrict())
                                    return ItemStack.isSameItemSameTags(stack, pouring.getOutput());
                                return ItemStack.isSameItem(stack, pouring.getOutput());
                            }).findFirst();
                            if (optionalData.isPresent()) {
                                ItemStack extracted = kegMenu.blockEntity.extractInGui(kegMenu.blockEntity, stack, ItemStack.EMPTY, stack.getCount());
                                if (!inventory.add(i, extracted))
                                    inventory.player.drop(extracted, false);
                            }
                        }
                        if (fermentingRecipe.getFluidIngredient() != null)
                            intlist.removeInt(intlist.size() - 1);
                    }
                }
                this.clearGrid();
                this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), recipe, intlist.iterator(), k);
            }
        }
    }
}
