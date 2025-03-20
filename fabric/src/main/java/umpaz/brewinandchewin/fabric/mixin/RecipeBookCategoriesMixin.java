package umpaz.brewinandchewin.fabric.mixin;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import umpaz.brewinandchewin.client.recipebook.BnCRecipeBook;
import vectorwing.farmersdelight.refabricated.FDRecipeBookTypes;
import vectorwing.farmersdelight.refabricated.client.FDRecipeCategories;

import java.util.List;

@Mixin(RecipeBookCategories.class)
public class RecipeBookCategoriesMixin {
	@Inject(method = "getCategories", at = @At("HEAD"), cancellable = true)
	private static void brewinandchewin$getCustomCategories(RecipeBookType recipeBookType, CallbackInfoReturnable<List<RecipeBookCategories>> cir) {
		if (recipeBookType == BnCRecipeBook.FERMENTING.get())
			cir.setReturnValue(List.of(BnCRecipeBook.FERMENTING_SEARCH.get(), BnCRecipeBook.FERMENTING_MEALS.get(), BnCRecipeBook.FERMENTING_DRINKS.get()));
	}
}
