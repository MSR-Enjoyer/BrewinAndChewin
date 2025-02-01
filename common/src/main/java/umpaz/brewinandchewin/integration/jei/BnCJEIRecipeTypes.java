package umpaz.brewinandchewin.integration.jei;

import mezz.jei.api.recipe.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import umpaz.brewinandchewin.BrewinAndChewin;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

public class BnCJEIRecipeTypes {
    public static final RecipeType<KegFermentingPouringRecipe> FERMENTING = RecipeType.create(BrewinAndChewin.MODID, "fermenting", KegFermentingPouringRecipe.class);
   public static final RecipeType<CheeseAgingRecipe> AGING = RecipeType.create(BrewinAndChewin.MODID, "aging", CheeseAgingRecipe.class);

}
