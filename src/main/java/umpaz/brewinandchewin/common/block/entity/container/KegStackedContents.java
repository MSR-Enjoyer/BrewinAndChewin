package umpaz.brewinandchewin.common.block.entity.container;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.mixin.StackedContentsRecipePickerAccessor;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.List;

public class KegStackedContents extends StackedContents {
    public final KegMenu menu;
    public final RecipeManager recipeManager;

    public KegStackedContents(KegMenu menu, RecipeManager manager) {
        this.menu = menu;
        this.recipeManager = manager;
    }

    @Override
    public boolean canCraft(Recipe<?> recipe, @Nullable IntList stackingIndexList, int amount) {
        if (recipe instanceof KegFermentingRecipe fermentingRecipe && fermentingRecipe.getTemperature() != menu.getKegTemperature())
            return false;
        return (new RecipePicker(recipe)).tryPick(amount, stackingIndexList);
    }

    @Override
    public int getBiggestCraftableStack(Recipe<?> recipe, int amount, @Nullable IntList stackingIndexList) {
        return (new RecipePicker(recipe)).tryPickAll(amount, stackingIndexList);
    }

    public class RecipePicker extends StackedContents.RecipePicker {
        public RecipePicker(Recipe<?> recipe) {
            super(recipe);
            if (recipe instanceof KegFermentingRecipe fermentingRecipe && fermentingRecipe.getFluidIngredient() != null && !menu.kegTank.getFluid().isFluidEqual(fermentingRecipe.getFluidIngredient())) {
                List<Pair<ItemStack, Boolean>> fluidOutputStacks = recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream().filter(kegPouringRecipe -> kegPouringRecipe.getRawFluid().isSame(fermentingRecipe.getFluidIngredient().getRawFluid())).map(r -> Pair.of(r.getOutput(), r.isStrict())).toList();
                StackedContentsRecipePickerAccessor accessor = (StackedContentsRecipePickerAccessor)this;
                Ingredient ingredient = Ingredient.of(fluidOutputStacks.stream().map(Pair::getFirst).toArray(ItemStack[]::new));
                if (fluidOutputStacks.stream().anyMatch(Pair::getSecond)) {
                    ingredient = CompoundIngredient.of(fluidOutputStacks.stream().map(p -> {
                        if (p.getSecond())
                            return StrictNBTIngredient.of(p.getFirst());
                        return Ingredient.of(p.getFirst().getItem());
                    }).toArray(Ingredient[]::new));
                }
                ingredient.checkInvalidation();
                ingredient.getItems();
                ingredient.getStackingIds();
                accessor.brewinandchewin$getIngredients().add(ingredient);
                accessor.brewinandchewin$getIngredients().removeIf(Ingredient::isEmpty);

                accessor.brewinandchewin$setIngredientCount(accessor.brewinandchewin$getIngredients().size());
                accessor.brewinandchewin$setItems(accessor.brewinandchewin$invokeGetUniqueAvailableIngredientItems());
                accessor.brewinandchewin$setItemCount(accessor.brewinandchewin$getItems().length);
                accessor.brewinandchewin$setData(new BitSet(accessor.brewinandchewin$getIngredientCount() + accessor.brewinandchewin$getItemCount() + accessor.brewinandchewin$getIngredientCount() + accessor.brewinandchewin$getIngredientCount() * accessor.brewinandchewin$getItemCount()));

                for(int i = 0; i < accessor.brewinandchewin$getIngredients().size(); ++i) {
                    IntList intlist = accessor.brewinandchewin$getIngredients().get(i).getStackingIds();

                    for(int j = 0; j < accessor.brewinandchewin$getItemCount(); ++j) {
                        if (intlist.contains(accessor.brewinandchewin$getItems()[j])) {
                            int bitIndex = accessor.brewinandchewin$getIndex(true, j, i);
                            accessor.brewinandchewin$getData().set(bitIndex);
                        }
                    }
                }
            }
        }

        public KegStackedContents getOuter() {
            return KegStackedContents.this;
        }
    }
}
