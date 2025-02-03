package umpaz.brewinandchewin.common.block.entity.container;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.mixin.StackedContentsRecipePickerAccessor;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

public class KegStackedContents extends StackedContents {
    public final KegMenu menu;
    public final RecipeManager recipeManager;
    private boolean ignoreItems = false;
    private boolean ignoreFluids = false;

    public KegStackedContents(KegMenu menu, RecipeManager manager) {
        this.menu = menu;
        this.recipeManager = manager;
    }

    public void setIgnoreItems(boolean value) {
        ignoreItems = value;
    }

    public void setIgnoreFluids(boolean value) {
        ignoreFluids = value;
    }

    public boolean shouldIgnoreItems() {
        return ignoreItems;
    }

    public boolean isFluidItem(Recipe<?> recipe, int itemIndex) {
        return (new KegStackedContents.RecipePicker(recipe)).isFluidItem(itemIndex);
    }

    @Override
    public boolean canCraft(Recipe<?> recipe, @Nullable IntList stackingIndexList, int amount) {
        if (recipe instanceof KegFermentingRecipe fermentingRecipe && !KegBlockEntity.isValidTemp(menu.getKegTemperature(), fermentingRecipe.getTemperature()))
            return false;
        return (new KegStackedContents.RecipePicker(recipe)).tryPick(amount, stackingIndexList);
    }

    @Override
    public int getBiggestCraftableStack(RecipeHolder<?> recipe, int amount, @Nullable IntList stackingIndexList) {
        return (new KegStackedContents.RecipePicker(recipe.value())).tryPickAll(amount, stackingIndexList);
    }

    public class RecipePicker extends StackedContents.RecipePicker {
        private final Int2IntMap stackCountRequirements = new Int2IntArrayMap();

        public RecipePicker(Recipe<?> recipe) {
            super(recipe);
            if (recipe instanceof KegFermentingRecipe fermentingRecipe) {
                boolean modified = false;
                AbstractedFluidTank kegTank = menu.kegTank;
                StackedContentsRecipePickerAccessor accessor = (StackedContentsRecipePickerAccessor)this;

                if (ignoreItems) {
                    accessor.brewinandchewin$getIngredients().clear();
                    modified = true;
                }

                if (!ignoreFluids) {
                    if (fermentingRecipe.getFluidIngredient().isEmpty() && !kegTank.isEmpty()) {
                        List<PouringEntry> fluidContainerStacks = recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING).stream().map(RecipeHolder::value)
                                .filter(kegPouringRecipe -> kegPouringRecipe.getRawFluid().fluid().isSame(kegTank.getAbstractedFluid().fluid())).map(r -> new PouringEntry(r.getContainer(), r.getRawFluid().amount(), r.isStrict())).toList();
                        if (!fluidContainerStacks.isEmpty()) {
                            for (PouringEntry entry : fluidContainerStacks) {
                                int itemAmount = kegTank.getAbstractedFluid().amount() / entry.fluidAmount();
                                stackCountRequirements.put(StackedContents.getStackingIndex(entry.stack()), itemAmount);
                            }
                            Ingredient extractIngredient = Ingredient.of(fluidContainerStacks.stream().map(PouringEntry::stack).toArray(ItemStack[]::new));
                            extractIngredient.getItems();
                            extractIngredient.getStackingIds();
                            accessor.brewinandchewin$getIngredients().add(extractIngredient);
                            modified = true;
                        }
                    } else if (fermentingRecipe.getFluidIngredient().isPresent()) {
                        List<PouringEntry> fluidOutputStacks = recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING).stream().map(RecipeHolder::value)
                                .filter(kegPouringRecipe -> kegPouringRecipe.canFill() && fermentingRecipe.getFluidIngredient().get().ingredient().matches(kegPouringRecipe.getRawFluid())).map(r -> new PouringEntry(r.getOutput(), r.getRawFluid().amount(), r.isStrict())).collect(Collectors.toCollection(ArrayList::new));

                        int tankAmount = kegTank.getAbstractedFluid().amount();

                        if (!kegTank.isEmpty() && !fermentingRecipe.getFluidIngredient().get().ingredient().matches(kegTank.getAbstractedFluid())) {
                            List<PouringEntry> fluidContainerStacks = recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING).stream().map(RecipeHolder::value)
                                    .filter(kegPouringRecipe -> kegPouringRecipe.getRawFluid().fluid().isSame(kegTank.getAbstractedFluid().fluid())).map(r -> new PouringEntry(r.getContainer(), r.getRawFluid().amount(), r.isStrict())).toList();
                            if (!fluidContainerStacks.isEmpty()) {
                                for (PouringEntry entry : fluidContainerStacks) {
                                    int itemAmount = kegTank.getAbstractedFluid().amount() / entry.fluidAmount();
                                    tankAmount -= itemAmount * entry.fluidAmount();
                                    stackCountRequirements.put(StackedContents.getStackingIndex(entry.stack()), itemAmount);
                                }
                                Ingredient extractIngredient = Ingredient.of(fluidContainerStacks.stream().map(PouringEntry::stack).toArray(ItemStack[]::new));
                                extractIngredient.getItems();
                                extractIngredient.getStackingIds();
                                accessor.brewinandchewin$getIngredients().add(extractIngredient);
                                modified = true;
                            }
                        }

                        if (!kegTank.isEmpty() && !fermentingRecipe.getFluidIngredient().get().ingredient().matches(kegTank.getAbstractedFluid()) || tankAmount < fermentingRecipe.getFluidIngredient().get().amount()) {
                            for (PouringEntry entry : List.copyOf(fluidOutputStacks)) {
                                int itemAmount = (Math.max(fermentingRecipe.getFluidIngredient().get().amount(), entry.fluidAmount() - tankAmount) / entry.fluidAmount()) - ((tankAmount % fermentingRecipe.getFluidIngredient().get().amount()) / entry.fluidAmount());
                                if (itemAmount <= 0 || (itemAmount * entry.fluidAmount()) + tankAmount > kegTank.getCapacity())
                                    fluidOutputStacks.remove(entry);
                                else
                                    stackCountRequirements.put(StackedContents.getStackingIndex(entry.stack()), itemAmount);
                            }
                            if (!fluidOutputStacks.isEmpty()) {
                                Ingredient fillIngredient = Ingredient.of(fluidOutputStacks.stream().map(PouringEntry::stack).toArray(ItemStack[]::new));
                                if (fluidOutputStacks.stream().anyMatch(PouringEntry::strict))
                                    fillIngredient = BrewinAndChewin.getHelper().createStrictFillPickerIngredient(fluidOutputStacks);
                                fillIngredient.getItems();
                                fillIngredient.getStackingIds();
                                accessor.brewinandchewin$getIngredients().add(fillIngredient);
                                modified = true;
                            }
                        }
                    }
                }

                if (!modified)
                    return;
                accessor.brewinandchewin$getIngredients().removeIf(Ingredient::isEmpty);

                accessor.brewinandchewin$setIngredientCount(accessor.brewinandchewin$getIngredients().size());
                accessor.brewinandchewin$setItems(accessor.brewinandchewin$invokeGetUniqueAvailableIngredientItems());
                accessor.brewinandchewin$setItemCount(accessor.brewinandchewin$getItems().length);
                accessor.brewinandchewin$setData(new BitSet(accessor.brewinandchewin$getIngredientCount() + accessor.brewinandchewin$getItemCount() + accessor.brewinandchewin$getIngredientCount() + accessor.brewinandchewin$getIngredientCount() * accessor.brewinandchewin$getItemCount()));

                for(int i = 0; i < accessor.brewinandchewin$getIngredients().size(); ++i) {
                    IntList intlist = accessor.brewinandchewin$getIngredients().get(i).getStackingIds();

                    for(int j = 0; j < accessor.brewinandchewin$getItemCount(); ++j) {
                        if (intlist.contains(accessor.brewinandchewin$getItems()[j])) {
                            int bitIndex = accessor.brewinandchewin$invokeGetIndex(true, j, i);
                            accessor.brewinandchewin$getData().set(bitIndex);
                        }
                    }
                }
            }
        }

        public boolean isFluidItem(int itemIndex) {
            return stackCountRequirements.containsKey(itemIndex);
        }

        public boolean hasFluidAmount(int originalAmount, int itemIndex) {
            return !isFluidItem(itemIndex) || originalAmount >= stackCountRequirements.get(itemIndex);
        }

        public KegStackedContents getOuter() {
            return KegStackedContents.this;
        }
    }

    public record PouringEntry(ItemStack stack, int fluidAmount, boolean strict) {}
}
