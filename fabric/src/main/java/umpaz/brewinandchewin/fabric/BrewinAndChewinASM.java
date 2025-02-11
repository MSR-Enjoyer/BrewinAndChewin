package umpaz.brewinandchewin.fabric;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import umpaz.brewinandchewin.common.registry.BnCItems;
import vectorwing.farmersdelight.common.registry.ModItems;

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
        ClassTinkerers.enumBuilder(recipeBookCategoriesTarget, itemStackParamType).addEnum(FERMENTING_SEARCH_RECIPE_BOOK_CATEGORY, () -> new Object[]{new ItemStack[]{new ItemStack(Items.COMPASS)}}).build();
        ClassTinkerers.enumBuilder(recipeBookCategoriesTarget, itemStackParamType).addEnum(FERMENTING_DRINKS_RECIPE_BOOK_CATEGORY, () -> new Object[]{new ItemStack[]{new ItemStack(BnCItems.BEER)}}).build();
        ClassTinkerers.enumBuilder(recipeBookCategoriesTarget, itemStackParamType).addEnum(FERMENTING_MEALS_RECIPE_BOOK_CATEGORY,() -> new Object[]{new ItemStack[]{new ItemStack(BnCItems.UNRIPE_FLAXEN_CHEESE_WHEEL)}}).build();
    }

}
