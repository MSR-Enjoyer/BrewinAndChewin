package umpaz.brewinandchewin.client.recipebook;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;

import java.util.function.Supplier;

public class BnCRecipeBook {
    public static RecipeBookType FERMENTING;

    public static Supplier<RecipeBookCategories> FERMENTING_SEARCH;
    public static Supplier<RecipeBookCategories> FERMENTING_DRINKS;
    public static Supplier<RecipeBookCategories> FERMENTING_MEALS;
}
