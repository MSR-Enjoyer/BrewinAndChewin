package umpaz.brewinandchewin.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.block.entity.container.AbstractedFluidTank;
import umpaz.brewinandchewin.common.block.entity.container.KegStackedContents;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(StackedContents.RecipePicker.class)
public class KegStackedContentsRecipePickerMixin {
    @Shadow @Final private Recipe<?> recipe;

    @Shadow @Final private int[] items;

    @ModifyExpressionValue(method = "dfs", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/Int2IntMap;get(I)I"))
    private int brewinandchewin$effectivelyMultiplyDfsAmount(int original, @Local(ordinal = 0, argsOnly = true) int amount, @Local(ordinal = 2) int j) {
        if ((StackedContents.RecipePicker)(Object)this instanceof KegStackedContents.RecipePicker kegRecipePicker) {
            if (!kegRecipePicker.hasFluidAmount(original, items[j]))
                return Integer.MIN_VALUE;
            else if (kegRecipePicker.isFluidItem(items[j]))
                return Integer.MAX_VALUE;
        }
        return original;
    }

    @WrapWithCondition(method = "tryPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/StackedContents;take(II)I"))
    private boolean brewinandchewin$dontTakeAwayFluidStack(StackedContents instance, int stackingIndex, int amount) {
        if ((StackedContents.RecipePicker)(Object)this instanceof KegStackedContents.RecipePicker kegRecipePicker) {
            return !kegRecipePicker.isFluidItem(stackingIndex);
        }
        return true;
    }

    @ModifyExpressionValue(method = "tryPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Recipe;getIngredients()Lnet/minecraft/core/NonNullList;"))
    private NonNullList<Ingredient> brewinandchewin$addExtraFluidContextToPick(NonNullList<Ingredient> original) {
        if ((StackedContents.RecipePicker)(Object)this instanceof KegStackedContents.RecipePicker kegRecipePicker && recipe instanceof KegFermentingRecipe fermentingRecipe) {
            AbstractedFluidTank kegTank = kegRecipePicker.getOuter().menu.kegTank;
            if (fermentingRecipe.getFluidIngredient().isEmpty() && !kegTank.isEmpty()) {
                List<ItemStack> fluidContainerStacks = kegRecipePicker.getOuter().recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING).stream()
                        .filter(kegPouringRecipe -> kegPouringRecipe.value().getRawFluid().fluid().isSame(kegTank.getAbstractedFluid().fluid())).map(pouringRecipe -> pouringRecipe.value().getContainer()).toList();
                if (!fluidContainerStacks.isEmpty()) {
                    Ingredient ingredient = Ingredient.of(fluidContainerStacks.toArray(ItemStack[]::new));
                    ingredient.getItems();
                    ingredient.getStackingIds();
                    List<Ingredient> ingredients = new ArrayList<>(original.stream().filter(ingredient1 -> !ingredient1.isEmpty()).toList());

                    if (kegRecipePicker.getOuter().shouldIgnoreItems())
                        ingredients.clear();

                    ingredients.add(ingredient);
                    return NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new));
                }
            } else if (fermentingRecipe.getFluidIngredient().isPresent()) {
                List<Ingredient> ingredients = new ArrayList<>(original.stream().filter(ingredient1 -> !ingredient1.isEmpty()).toList());

                if (kegRecipePicker.getOuter().shouldIgnoreItems())
                    ingredients.clear();

                long tankAmount = kegTank.getAbstractedFluid().amount();

                if (!kegTank.isEmpty() && !fermentingRecipe.getFluidIngredient().get().ingredient().matches(kegTank.getAbstractedFluid())) {
                    List<KegStackedContents.PouringEntry> fluidContainerStacks = kegRecipePicker.getOuter().recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING).stream().map(RecipeHolder::value)
                            .filter(kegPouringRecipe -> kegPouringRecipe.getRawFluid().fluid().isSame(kegTank.getAbstractedFluid().fluid())).map(r -> new KegStackedContents.PouringEntry(r.getContainer(), r.getRawFluid().amount(), r.getUnit(), r.isStrict())).toList();
                    if (!fluidContainerStacks.isEmpty()) {
                        Ingredient extractIngredient = Ingredient.of(fluidContainerStacks.stream().map(KegStackedContents.PouringEntry::stack).toArray(ItemStack[]::new));

                        for (KegStackedContents.PouringEntry entry : fluidContainerStacks)
                            tankAmount -= entry.fluidAmount();

                        extractIngredient.getItems();
                        extractIngredient.getStackingIds();
                        ingredients.add(extractIngredient);
                    }
                }

                if (!kegTank.isEmpty() && fermentingRecipe.getFluidIngredient().get().ingredient().matches(kegTank.getAbstractedFluid()) || tankAmount < fermentingRecipe.getFluidIngredient().get().amount()) {
                    List<KegStackedContents.PouringEntry> fluidOutputStacks = kegRecipePicker.getOuter().recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING).stream().map(RecipeHolder::value).filter(kegPouringRecipe -> kegPouringRecipe.canFill() && fermentingRecipe.getFluidIngredient().get().ingredient().matches(kegPouringRecipe.getRawFluid())).map(r -> new KegStackedContents.PouringEntry(r.getOutput(), r.getRawFluid().amount(), r.getUnit(), r.isStrict())).collect(Collectors.toCollection(ArrayList::new));
                    long finalTankAmount = tankAmount;
                    fluidOutputStacks.removeIf(entry -> {
                        int itemAmount = (int) ((Math.max(fermentingRecipe.getFluidIngredient().get().loaderAmount(), entry.fluidUnit().convertToLoader(entry.fluidAmount()) - finalTankAmount) / entry.fluidUnit().convertToLoader(entry.fluidAmount())) - ((finalTankAmount % fermentingRecipe.getFluidIngredient().get().loaderAmount()) / entry.fluidUnit().convertToLoader(entry.fluidAmount())));
                        return itemAmount <= 0 || (itemAmount * entry.fluidAmount()) + finalTankAmount > kegTank.getFluidCapacity();
                    });
                    if (!fluidOutputStacks.isEmpty()) {
                        Ingredient ingredient = Ingredient.of(fluidOutputStacks.stream().map(KegStackedContents.PouringEntry::stack).toArray(ItemStack[]::new));
                        if (fluidOutputStacks.stream().anyMatch(KegStackedContents.PouringEntry::strict)) {
                            BrewinAndChewin.getHelper().createStrictFillPickerIngredient(fluidOutputStacks);
                        }
                        ingredient.getItems();
                        ingredient.getStackingIds();
                        ingredients.add(ingredient);
                    }
                }

                return NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new));
            }
        }
        return original;
    }
}
