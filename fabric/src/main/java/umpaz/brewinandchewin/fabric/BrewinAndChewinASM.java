package umpaz.brewinandchewin.fabric;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import umpaz.brewinandchewin.common.registry.BnCItems;

import java.util.List;
import java.util.function.Supplier;

public class BrewinAndChewinASM implements Runnable {
    public static final String FERMENTING_RECIPE_BOOK_TYPE = "BREWINANDCHEWIN_FERMENTING";
    public static final String FERMENTING_SEARCH_RECIPE_BOOK_CATEGORY = "BREWINANDCHEWIN_FERMENTING_SEARCH";
    public static final String FERMENTING_DRINKS_RECIPE_BOOK_CATEGORY = "BREWINANDCHEWIN_FERMENTING_DRINKS";
    public static final String FERMENTING_MEALS_RECIPE_BOOK_CATEGORY = "BREWINANDCHEWIN_FERMENTING_MEALS";

    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
        String recipeBookTypeTarget = remapper.mapClassName("intermediary", "net.minecraft.class_5421");
        ClassTinkerers.enumBuilder(recipeBookTypeTarget).addEnum(FERMENTING_RECIPE_BOOK_TYPE).build();
        String recipeBookCategoriesTarget = remapper.mapClassName("intermediary", "net.minecraft.class_314");
        String itemStackParamType = "[L" + remapper.mapClassName("intermediary", "net.minecraft.class_1799") + ";";
        ClassTinkerers.enumBuilder(recipeBookCategoriesTarget, itemStackParamType).addEnum(FERMENTING_SEARCH_RECIPE_BOOK_CATEGORY, getSearchCategoryStacks()).build();
        ClassTinkerers.enumBuilder(recipeBookCategoriesTarget, itemStackParamType).addEnum(FERMENTING_DRINKS_RECIPE_BOOK_CATEGORY, getMealsCategoryStacks()).build();
        ClassTinkerers.enumBuilder(recipeBookCategoriesTarget, itemStackParamType).addEnum(FERMENTING_MEALS_RECIPE_BOOK_CATEGORY, getDrinksCategoryStacks()).build();
    }

    public static Supplier<Object[]> getSearchCategoryStacks() {
        // Requires a Supplier based workaround to make sure that ItemLike isn't loaded.
        // See https://github.com/MehVahdJukaar/FarmersDelightRefabricated/issues/77
        return () -> new Object[]{new ItemStack[]{new ItemStack(((Supplier<Item>)() -> Items.COMPASS).get())}};
    }

    public static Supplier<Object[]> getMealsCategoryStacks() {
        // Requires a Supplier based workaround to make sure that ItemLike isn't loaded.
        // See https://github.com/MehVahdJukaar/FarmersDelightRefabricated/issues/77
        return () -> new Object[]{new ItemStack[]{new ItemStack(((Supplier<Item>)() -> BnCItems.BEER).get())}};
    }
    public static Supplier<Object[]> getDrinksCategoryStacks() {
        // Requires a Supplier based workaround to make sure that ItemLike isn't loaded.
        // See https://github.com/MehVahdJukaar/FarmersDelightRefabricated/issues/77
        return () -> new Object[]{new ItemStack[]{new ItemStack(((Supplier<Item>)() -> BnCItems.UNRIPE_FLAXEN_CHEESE_WHEEL).get())}};
    }
}
