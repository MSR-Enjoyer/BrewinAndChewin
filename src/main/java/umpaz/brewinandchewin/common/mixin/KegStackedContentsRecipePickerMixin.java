package umpaz.brewinandchewin.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import umpaz.brewinandchewin.common.block.entity.container.KegStackedContents;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;

import java.util.ArrayList;
import java.util.List;

@Mixin(StackedContents.RecipePicker.class)
public class KegStackedContentsRecipePickerMixin {
    @Shadow @Final private Recipe<?> recipe;

    @Shadow @Final private int[] items;

    @ModifyExpressionValue(method = "dfs", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/Int2IntMap;get(I)I"))
    private int brewinandchewin$effectivelyMultiplyTheAmount(int original, @Local(ordinal = 0, argsOnly = true) int amount, @Local(ordinal = 2) int j) {
        if ((StackedContents.RecipePicker)(Object)this instanceof KegStackedContents.RecipePicker kegRecipePicker && !kegRecipePicker.hasMultipliedAmount(original, items[j], amount)) {
            return Integer.MIN_VALUE;
        }
        return original;
    }

    @ModifyVariable(method = "tryPick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/crafting/Recipe;getIngredients()Lnet/minecraft/core/NonNullList;"))
    private List<Ingredient> brewinandchewin$addExtraFluidContextToPick(List<Ingredient> original) {
        if ((StackedContents.RecipePicker)(Object)this instanceof KegStackedContents.RecipePicker kegRecipePicker && recipe instanceof KegFermentingRecipe fermentingRecipe) {
            FluidTank kegTank = kegRecipePicker.getOuter().menu.kegTank;
            if (fermentingRecipe.getFluidIngredient() == null) {
                List<ItemStack> fluidContainerStacks = kegRecipePicker.getOuter().recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream()
                        .filter(kegPouringRecipe -> kegPouringRecipe.getRawFluid().isSame(kegTank.getFluid().getRawFluid())).map(KegPouringRecipe::getContainer).toList();
                if (!fluidContainerStacks.isEmpty()) {
                    Ingredient ingredient = Ingredient.of(fluidContainerStacks.toArray(ItemStack[]::new));
                    ingredient.getItems();
                    ingredient.getStackingIds();
                    List<Ingredient> ingredients = new ArrayList<>(original);
                    ingredients.add(ingredient);
                    return ingredients;
                }
            } else if (fermentingRecipe.getFluidIngredient() != null && (!kegTank.getFluid().isFluidEqual(fermentingRecipe.getFluidIngredient()) || kegTank.getFluidAmount() % fermentingRecipe.getAmount() == 0 && kegTank.getFluidAmount() < kegTank.getCapacity())) {
                List<Ingredient> ingredients = new ArrayList<>(original);
                List<Pair<ItemStack, Boolean>> fluidOutputStacks = kegRecipePicker.getOuter().recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream().filter(kegPouringRecipe -> kegPouringRecipe.getRawFluid().isSame(fermentingRecipe.getFluidIngredient().getRawFluid())).map(r -> Pair.of(r.getOutput(), r.isStrict())).toList();
                if (!fluidOutputStacks.isEmpty()) {
                    Ingredient ingredient = Ingredient.of(fluidOutputStacks.stream().map(Pair::getFirst).toArray(ItemStack[]::new));
                    if (fluidOutputStacks.stream().anyMatch(Pair::getSecond)) {
                        ingredient = CompoundIngredient.of(fluidOutputStacks.stream().map(p -> {
                            if (p.getSecond())
                                return StrictNBTIngredient.of(p.getFirst());
                            return Ingredient.of(p.getFirst().getItem());
                        }).toArray(Ingredient[]::new));
                    }
                    ingredient.getItems();
                    ingredient.getStackingIds();
                    ingredients.add(ingredient);
                }

                if (!kegTank.isEmpty()) {
                    List<ItemStack> fluidContainerStacks = kegRecipePicker.getOuter().recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream()
                            .filter(kegPouringRecipe -> kegPouringRecipe.getRawFluid().isSame(kegTank.getFluid().getFluid())).map(KegPouringRecipe::getContainer).toList();
                    if (!fluidContainerStacks.isEmpty()) {
                        Ingredient extractIngredient = Ingredient.of(fluidContainerStacks.toArray(ItemStack[]::new));
                        extractIngredient.getItems();
                        extractIngredient.getStackingIds();
                        ingredients.add(extractIngredient);
                    }
                }

                return ingredients;
            }
        }
        return original;
    }
}
