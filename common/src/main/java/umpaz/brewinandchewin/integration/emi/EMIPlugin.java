package umpaz.brewinandchewin.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.utility.BnCFluidItemDisplays;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.registry.BnCItems;
import umpaz.brewinandchewin.common.registry.BnCMenuTypes;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.FluidUnit;
import umpaz.brewinandchewin.integration.emi.handler.KegEmiRecipeHandler;
import umpaz.brewinandchewin.integration.emi.recipe.CheeseEmiRecipe;
import umpaz.brewinandchewin.integration.emi.recipe.FermentingEmiRecipe;

import java.util.List;

@EmiEntrypoint
public class EMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(BnCRecipeCategories.FERMENTING);
        registry.addCategory(BnCRecipeCategories.AGING);

        registry.addWorkstation(BnCRecipeCategories.FERMENTING, BnCRecipeWorkstations.KEG);
        registry.addRecipeHandler(BnCMenuTypes.KEG, new KegEmiRecipeHandler());

        for (RecipeHolder<KegFermentingRecipe> recipe : registry.getRecipeManager().getAllRecipesFor(BnCRecipeTypes.FERMENTING)) {
            if (recipe.value().getResult().left().isPresent()) {
                AbstractedFluidStack stack = recipe.value().getResult().left().get();
                List<RecipeHolder<KegPouringRecipe>> pouringRecipes = registry.getRecipeManager().getAllRecipesFor(BnCRecipeTypes.KEG_POURING).stream().filter(holder -> holder.value().getRawFluid().matches(stack)).toList();
                for (RecipeHolder<KegPouringRecipe> pouringRecipe : pouringRecipes) {
                    ResourceLocation id = ItemStack.isSameItemSameComponents(pouringRecipe.value().getOutput(), BnCFluidItemDisplays.getFluidItemDisplay(Minecraft.getInstance().level.registryAccess(), pouringRecipe.value().getRawFluid())) ?
                            recipe.id() :
                            recipe.id().withPath(string -> "/" + string + "_" + pouringRecipe.id().getNamespace() + "_" + pouringRecipe.id().getPath());
                    registry.addRecipe(new FermentingEmiRecipe(id, recipe.value().getIngredients().stream().map(EmiIngredient::of).toList(),
                            getFluidIngredient(recipe), EmiStack.of(stack.fluid(), stack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY, stack.unit().convertToLoader(stack.amount())),
                            EmiStack.of(pouringRecipe.value().getOutput()), EmiStack.of(pouringRecipe.value().getContainer()), recipe.value().getTemperature(), recipe.value().getFermentTime(), recipe.value().getExperience()));
                }
            } else {
                registry.addRecipe(new FermentingEmiRecipe(recipe.id(), recipe.value().getIngredients().stream().map(EmiIngredient::of).toList(),
                        getFluidIngredient(recipe), null,
                        EmiStack.of(recipe.value().getResultItem(Minecraft.getInstance().level.registryAccess())), null, recipe.value().getTemperature(), recipe.value().getFermentTime(), recipe.value().getExperience()));
            }
        }

        registry.addRecipe(new CheeseEmiRecipe(BrewinAndChewin.asResource("/cheese/flaxen"), EmiStack.of(BnCItems.UNRIPE_FLAXEN_CHEESE_WHEEL.getDefaultInstance()), EmiStack.of(BnCItems.FLAXEN_CHEESE_WHEEL.getDefaultInstance())));
        registry.addRecipe(new CheeseEmiRecipe(BrewinAndChewin.asResource("/cheese/flaxen"), EmiStack.of(BnCItems.UNRIPE_SCARLET_CHEESE_WHEEL.getDefaultInstance()), EmiStack.of(BnCItems.SCARLET_CHEESE_WHEEL.getDefaultInstance())));
    }

    private EmiIngredient getFluidIngredient(RecipeHolder<KegFermentingRecipe> recipe) {
        if (recipe.value().getFluidIngredient().isEmpty())
            return null;
        return EmiIngredient.of(recipe.value().getFluidIngredient().orElseThrow().ingredient().displayStacks().stream().map(stack -> EmiStack.of(stack.fluid(), stack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY, stack.unit().convertToLoader(stack.amount()))).toList());
    }
}
