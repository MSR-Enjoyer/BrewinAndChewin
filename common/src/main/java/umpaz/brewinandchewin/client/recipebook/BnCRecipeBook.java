package umpaz.brewinandchewin.client.recipebook;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;

import java.util.function.Supplier;

public class BnCRecipeBook {
    public static Supplier<RecipeBookType> FERMENTING = () -> RecipeBookType.valueOf("BREWINANDCHEWIN_FERMENTING");

    public static Supplier<RecipeBookCategories> FERMENTING_SEARCH = () -> RecipeBookCategories.valueOf("BREWINANDCHEWIN_FERMENTING_SEARCH");
    public static Supplier<RecipeBookCategories> FERMENTING_DRINKS = () -> RecipeBookCategories.valueOf("BREWINANDCHEWIN_FERMENTING_DRINKS");
    public static Supplier<RecipeBookCategories> FERMENTING_MEALS = () -> RecipeBookCategories.valueOf("BREWINANDCHEWIN_FERMENTING_MEALS");
}
