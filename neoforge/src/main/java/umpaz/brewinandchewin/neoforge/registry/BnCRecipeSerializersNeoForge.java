package umpaz.brewinandchewin.neoforge.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.neoforge.crafting.CreatePotionPouringRecipe;

// TODO: Move me into common when Fabric gets build artifacts.
public class BnCRecipeSerializersNeoForge {
    public static final RecipeSerializer<?> CREATE_POTION_POURING = createCreatePotionPouringRecipe();

    public static void registerAll() {
        if (CREATE_POTION_POURING != null)
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, BrewinAndChewin.asResource("create_potion_pouring"), CREATE_POTION_POURING);
    }

    private static RecipeSerializer<?> createCreatePotionPouringRecipe() {
        if (BrewinAndChewin.getHelper().isModLoaded("create"))
            return new CreatePotionPouringRecipe.Serializer();
        return null;
    }
}
