package umpaz.brewinandchewin.fabric.mixin.client;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import umpaz.brewinandchewin.client.recipebook.BnCRecipeBookCategories;
import umpaz.brewinandchewin.common.BnCRecipeBookTypes;

import java.util.List;

@Mixin(RecipeBookCategories.class)
public class RecipeBookCategoriesMixin {
	@Inject(method = "getCategories", at = @At("HEAD"), cancellable = true)
	private static void brewinandchewin$getCustomCategories(RecipeBookType recipeBookType, CallbackInfoReturnable<List<RecipeBookCategories>> cir) {
		if (recipeBookType == BnCRecipeBookTypes.FERMENTING)
			cir.setReturnValue(List.of(BnCRecipeBookCategories.FERMENTING_SEARCH, BnCRecipeBookCategories.FERMENTING_MEALS, BnCRecipeBookCategories.FERMENTING_DRINKS));
	}
}
