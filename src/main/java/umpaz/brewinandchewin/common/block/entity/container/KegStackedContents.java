package umpaz.brewinandchewin.common.block.entity.container;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.mixin.StackedContentsRecipePickerAccessor;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.integration.jei.category.FermentingRecipeCategory;

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
    public int getBiggestCraftableStack(Recipe<?> recipe, int amount, @Nullable IntList stackingIndexList) {
        return (new KegStackedContents.RecipePicker(recipe)).tryPickAll(amount, stackingIndexList);
    }

    public class RecipePicker extends StackedContents.RecipePicker {
        private final Int2IntMap stackCountRequirements = new Int2IntArrayMap();

        public RecipePicker(Recipe<?> recipe) {
            super(recipe);
            if (recipe instanceof KegFermentingRecipe fermentingRecipe) {
                boolean modified = false;
                FluidTank kegTank = menu.kegTank;
                StackedContentsRecipePickerAccessor accessor = (StackedContentsRecipePickerAccessor)this;

                if (ignoreItems) {
                    accessor.brewinandchewin$getIngredients().clear();
                    modified = true;
                }

                if (!ignoreFluids) {
                    if (fermentingRecipe.getFluidIngredient() == null && !kegTank.isEmpty()) {
                        List<PouringEntry> fluidContainerStacks = recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream()
                                .filter(kegPouringRecipe -> kegPouringRecipe.getRawFluid().isSame(kegTank.getFluid().getRawFluid())).map(r -> new PouringEntry(r.getContainer(), r.getAmount(), r.isStrict())).toList();
                        if (!fluidContainerStacks.isEmpty()) {
                            for (PouringEntry entry : fluidContainerStacks) {
                                int itemAmount = kegTank.getFluid().getAmount() / entry.fluidAmount();
                                stackCountRequirements.put(StackedContents.getStackingIndex(entry.stack()), itemAmount);
                            }
                            Ingredient extractIngredient = Ingredient.of(fluidContainerStacks.stream().map(PouringEntry::stack).toArray(ItemStack[]::new));
                            extractIngredient.checkInvalidation();
                            extractIngredient.getItems();
                            extractIngredient.getStackingIds();
                            accessor.brewinandchewin$getIngredients().add(extractIngredient);
                            modified = true;
                        }
                    } else if (fermentingRecipe.getFluidIngredient() != null) {
                        List<PouringEntry> fluidOutputStacks = recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream().filter(kegPouringRecipe -> kegPouringRecipe.getRawFluid().isSame(fermentingRecipe.getFluidIngredient().getRawFluid())).map(r -> new PouringEntry(r.getOutput(), r.getAmount(), r.isStrict())).collect(Collectors.toCollection(ArrayList::new));
                        if (!kegTank.isEmpty() && !kegTank.getFluid().isFluidEqual(fermentingRecipe.getFluidIngredient()) || kegTank.getFluidAmount() < fermentingRecipe.getFluidIngredient().getAmount()) {
                            for (PouringEntry entry : List.copyOf(fluidOutputStacks)) {
                                int itemAmount = (fermentingRecipe.getFluidIngredient().getAmount() / entry.fluidAmount()) - ((kegTank.getFluidAmount() % fermentingRecipe.getFluidIngredient().getAmount()) / entry.fluidAmount());
                                if (itemAmount <= 0 || (itemAmount * entry.fluidAmount()) + kegTank.getFluidAmount() > kegTank.getCapacity())
                                    fluidOutputStacks.remove(entry);
                                else
                                    stackCountRequirements.put(StackedContents.getStackingIndex(entry.stack()), itemAmount);
                            }
                            if (!fluidOutputStacks.isEmpty()) {
                                Ingredient fillIngredient = Ingredient.of(fluidOutputStacks.stream().map(PouringEntry::stack).toArray(ItemStack[]::new));
                                if (fluidOutputStacks.stream().anyMatch(PouringEntry::strict)) {
                                    fillIngredient = CompoundIngredient.of(fluidOutputStacks.stream().map(p -> {
                                        if (p.strict())
                                            return StrictNBTIngredient.of(p.stack());
                                        return Ingredient.of(p.stack().getItem());
                                    }).toArray(Ingredient[]::new));
                                }
                                fillIngredient.getItems();
                                fillIngredient.getStackingIds();
                                accessor.brewinandchewin$getIngredients().add(fillIngredient);
                                modified = true;
                            }
                        }

                        if (!kegTank.isEmpty() && !kegTank.getFluid().isFluidEqual(fermentingRecipe.getFluidIngredient())) {
                            List<PouringEntry> fluidContainerStacks = recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream()
                                    .filter(kegPouringRecipe -> kegPouringRecipe.getRawFluid().isSame(kegTank.getFluid().getRawFluid())).map(r -> new PouringEntry(r.getContainer(), r.getAmount(), r.isStrict())).toList();
                            if (!fluidContainerStacks.isEmpty()) {
                                for (PouringEntry entry : fluidOutputStacks) {
                                    int itemAmount = kegTank.getFluid().getAmount() / entry.fluidAmount();
                                    stackCountRequirements.put(StackedContents.getStackingIndex(entry.stack()), itemAmount);
                                }
                                Ingredient extractIngredient = Ingredient.of(fluidContainerStacks.stream().map(PouringEntry::stack).toArray(ItemStack[]::new));
                                extractIngredient.getItems();
                                extractIngredient.getStackingIds();
                                accessor.brewinandchewin$getIngredients().add(extractIngredient);
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
