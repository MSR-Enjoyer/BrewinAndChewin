package umpaz.brewinandchewin.fabric.client;

import com.google.common.collect.ImmutableList;
import io.github.fabricators_of_create.porting_lib.recipe_book_categories.RecipeBookRegistry;
import umpaz.brewinandchewin.client.recipebook.BnCRecipeBook;
import umpaz.brewinandchewin.client.recipebook.FermentingBookCategory;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;

public class BnCRecipeCategories {
    public static void init() {
        RecipeBookRegistry.registerBookCategories(BnCRecipeBook.FERMENTING.get(), ImmutableList.of(BnCRecipeBook.FERMENTING_SEARCH.get(), BnCRecipeBook.FERMENTING_DRINKS.get(), BnCRecipeBook.FERMENTING_MEALS.get()));
        RecipeBookRegistry.registerAggregateCategory(BnCRecipeBook.FERMENTING_SEARCH.get(), ImmutableList.of(BnCRecipeBook.FERMENTING_DRINKS.get(), BnCRecipeBook.FERMENTING_MEALS.get()));
        RecipeBookRegistry.registerRecipeCategoryFinder(BnCRecipeTypes.FERMENTING, recipe ->
        {
            if (recipe.value() instanceof KegFermentingRecipe cookingRecipe) {
                FermentingBookCategory category = cookingRecipe.getRecipeBookCategory();
                if (category != null) {
                    return switch (category) {
                        case MEALS -> BnCRecipeBook.FERMENTING_MEALS.get();
                        case DRINKS -> BnCRecipeBook.FERMENTING_DRINKS.get();
                    };
                }
            }
            return BnCRecipeBook.FERMENTING_DRINKS.get();
        });
        RecipeBookRegistry.registerRecipeCategoryFinder(BnCRecipeTypes.KEG_POURING, recipeHolder -> null);
    }
}