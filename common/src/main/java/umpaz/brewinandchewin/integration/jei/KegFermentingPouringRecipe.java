package umpaz.brewinandchewin.integration.jei;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;

import javax.annotation.Nullable;

/**
 * look. this is only to show #{KegPouringRecipe} and @{KegFermentingRecipe
 */
public class KegFermentingPouringRecipe extends KegFermentingRecipe {

    private final ResourceLocation id;
    private final ItemStack catalyst;
    private ItemStack output;

    private final int catalystAmount;

    KegFermentingPouringRecipe(ResourceLocation id, KegFermentingRecipe fermentingRecipe, @Nullable KegPouringRecipe pouringRecipe, HolderLookup.Provider provider) {
        super(fermentingRecipe.getIngredients(), fermentingRecipe.getRecipeBookCategory(), fermentingRecipe.getFluidIngredient(), fermentingRecipe.getResult(), fermentingRecipe.getExperience(), fermentingRecipe.getFermentTime(), fermentingRecipe.getTemperature());
        if (fermentingRecipe.getResult().right().isPresent()) {
            this.output = fermentingRecipe.getResult().right().get().copy();
        } else if (pouringRecipe != null) {
            this.output = pouringRecipe.getOutput();
        }

        if (pouringRecipe != null) {
            this.catalyst = pouringRecipe.getContainer();
            this.catalystAmount = pouringRecipe.getResultItem(provider).getCount();
        } else {
            this.catalyst = null;
            this.catalystAmount = 0;
        }
        this.id = id;
    }

    public ResourceLocation getId() {
        return id;
    }

    public ItemStack getCatalyst() {
        return this.catalyst;
    }

    public int getCatalystAmount() {
        return this.catalystAmount;
    }


    public ItemStack getOutput() {
        return this.output;
    }
}