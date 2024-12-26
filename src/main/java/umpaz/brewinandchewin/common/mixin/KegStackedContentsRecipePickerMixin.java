package umpaz.brewinandchewin.common.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import umpaz.brewinandchewin.common.block.entity.container.KegStackedContents;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;

import java.util.ArrayList;
import java.util.List;

@Mixin(StackedContents.RecipePicker.class)
public class KegStackedContentsRecipePickerMixin {
    @Shadow @Final private Recipe<?> recipe;

    @ModifyVariable(method = "tryPick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/crafting/Recipe;getIngredients()Lnet/minecraft/core/NonNullList;"))
    private List<Ingredient> brewinandchewin$addExtraFluidContextToPick(List<Ingredient> original) {
        if ((StackedContents.RecipePicker)(Object)this instanceof KegStackedContents.RecipePicker kegRecipePicker) {
            if (recipe instanceof KegFermentingRecipe fermentingRecipe && fermentingRecipe.getFluidIngredient() != null && !kegRecipePicker.getOuter().menu.kegTank.getFluid().isFluidEqual(fermentingRecipe.getFluidIngredient())) {
                List<Pair<ItemStack, Boolean>> fluidOutputStacks = kegRecipePicker.getOuter().recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream().filter(kegPouringRecipe -> kegPouringRecipe.getRawFluid().isSame(fermentingRecipe.getFluidIngredient().getRawFluid())).map(r -> Pair.of(r.getOutput(), r.isStrict())).toList();
                Ingredient ingredient = Ingredient.of(fluidOutputStacks.stream().map(Pair::getFirst).toArray(ItemStack[]::new));
                if (fluidOutputStacks.stream().anyMatch(Pair::getSecond)) {
                    ingredient = CompoundIngredient.of(fluidOutputStacks.stream().map(p -> {
                        if (p.getSecond())
                            return StrictNBTIngredient.of(p.getFirst());
                        return Ingredient.of(p.getFirst().getItem());
                    }).toArray(Ingredient[]::new));
                }
                List<Ingredient> ingredients = new ArrayList<>(original);
                ingredients.add(ingredient);
                return ingredients;
            }
        }
        return original;
    }
}
