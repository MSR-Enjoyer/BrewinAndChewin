package umpaz.brewinandchewin.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;

public class BnCRecipeSerializers {
    public static final RecipeSerializer<?> FERMENTING = new KegFermentingRecipe.Serializer();
    public static final RecipeSerializer<?> KEG_POURING = new KegPouringRecipe.Serializer();
//    public static final RegistryObject<RecipeSerializer<?>> CREATE_POTION_POURING = createCreatePotionPouringRecipe();

    // TODO: Re-add when Create updates.
//    private static RegistryObject<RecipeSerializer<?>> createCreatePotionPouringRecipe() {
//        if (ModList.get().isLoaded("create"))
//            return RECIPE_SERIALIZERS.register("create_potion_pouring", CreatePotionPouringRecipe.Serializer::new);
//        return null;
//    }

    public static void registerAll() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, BrewinAndChewin.asResource("fermenting"), FERMENTING);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, BrewinAndChewin.asResource("keg_pouring"), KEG_POURING);
    }

}
