package umpaz.brewinandchewin.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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

                    if (kegRecipePicker.getOuter().shouldIgnoreItems())
                        ingredients.clear();

                    ingredients.add(ingredient);
                    return ingredients;
                }
            } else if (fermentingRecipe.getFluidIngredient() != null) {
                List<Ingredient> ingredients = new ArrayList<>(original);

                if (kegRecipePicker.getOuter().shouldIgnoreItems())
                    ingredients.clear();

                if (!kegTank.isEmpty() && !kegTank.getFluid().isFluidEqual(fermentingRecipe.getFluidIngredient()) || kegTank.getFluidAmount() < fermentingRecipe.getFluidIngredient().getAmount()) {
                    List<KegStackedContents.PouringEntry> fluidOutputStacks = kegRecipePicker.getOuter().recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream().filter(kegPouringRecipe -> kegPouringRecipe.canFill() && kegPouringRecipe.getRawFluid().isSame(fermentingRecipe.getFluidIngredient().getRawFluid())).map(r -> new KegStackedContents.PouringEntry(r.getOutput(), r.getAmount(), r.isStrict())).collect(Collectors.toCollection(ArrayList::new));
                    fluidOutputStacks.removeIf(entry -> {
                        int itemAmount = (fermentingRecipe.getFluidIngredient().getAmount() / entry.fluidAmount()) - ((kegTank.getFluidAmount() % fermentingRecipe.getFluidIngredient().getAmount()) / entry.fluidAmount());
                        return itemAmount <= 0 || (itemAmount * entry.fluidAmount()) + kegTank.getFluidAmount() > kegTank.getCapacity();
                    });
                    if (!fluidOutputStacks.isEmpty()) {
                        Ingredient ingredient = Ingredient.of(fluidOutputStacks.stream().map(KegStackedContents.PouringEntry::stack).toArray(ItemStack[]::new));
                        if (fluidOutputStacks.stream().anyMatch(KegStackedContents.PouringEntry::strict)) {
                            ingredient = CompoundIngredient.of(fluidOutputStacks.stream().map(p -> {
                                if (p.strict())
                                    return StrictNBTIngredient.of(p.stack());
                                return Ingredient.of(p.stack().getItem());
                            }).toArray(Ingredient[]::new));
                        }
                        ingredient.getItems();
                        ingredient.getStackingIds();
                        ingredients.add(ingredient);
                    }
                }

                if (!kegTank.isEmpty() && !kegTank.getFluid().isFluidEqual(fermentingRecipe.getFluidIngredient())) {
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
