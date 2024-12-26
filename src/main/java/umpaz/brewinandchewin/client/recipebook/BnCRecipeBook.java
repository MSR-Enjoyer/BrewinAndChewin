package umpaz.brewinandchewin.client.recipebook;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.registry.BnCItems;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = BrewinAndChewin.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BnCRecipeBook {
    public static final RecipeBookType FERMENTING = RecipeBookType.create("FERMENTING");

    public static final Supplier<RecipeBookCategories> FERMENTING_SEARCH = Suppliers.memoize(() -> RecipeBookCategories.create("FERMENTING_SEARCH", new ItemStack(Items.COMPASS)));
    public static final Supplier<RecipeBookCategories> FERMENTING_DRINKS = Suppliers.memoize(() -> RecipeBookCategories.create("FERMENTING_DRINKS", new ItemStack(BnCItems.BEER.get())));
    public static final Supplier<RecipeBookCategories> FERMENTING_MEALS = Suppliers.memoize(() -> RecipeBookCategories.create("FERMENTING_MEALS", new ItemStack(BnCItems.UNRIPE_FLAXEN_CHEESE_WHEEL.get())));

    @SubscribeEvent
    public static void init(RegisterRecipeBookCategoriesEvent event) {
        event.registerBookCategories(FERMENTING, ImmutableList.of(FERMENTING_SEARCH.get(), FERMENTING_DRINKS.get(), FERMENTING_MEALS.get()));
        event.registerAggregateCategory(FERMENTING_SEARCH.get(), ImmutableList.of(FERMENTING_DRINKS.get(), FERMENTING_MEALS.get()));
        event.registerRecipeCategoryFinder(BnCRecipeTypes.FERMENTING.get(), recipe ->
        {
            if (recipe instanceof KegFermentingRecipe fermentingRecipe) {
                FermentingRecipeBookTab tab = fermentingRecipe.getRecipeBookTab();
                if (tab != null) {
                    return switch (tab) {
                        case MEALS -> FERMENTING_MEALS.get();
                        case DRINKS -> FERMENTING_DRINKS.get();
                    };
                }
            }
            return null;
        });
        event.registerRecipeCategoryFinder(BnCRecipeTypes.KEG_POURING.get(), recipe -> RecipeBookCategories.UNKNOWN);
    }
}
