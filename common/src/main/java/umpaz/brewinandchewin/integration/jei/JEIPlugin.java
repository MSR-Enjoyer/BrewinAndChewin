package umpaz.brewinandchewin.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.gui.KegScreen;
import umpaz.brewinandchewin.common.registry.BnCItems;
import umpaz.brewinandchewin.integration.jei.category.CheeseRecipeCategory;
import umpaz.brewinandchewin.integration.jei.category.FermentingRecipeCategory;
import umpaz.brewinandchewin.integration.jei.transfer.FermentingTransfer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@JeiPlugin
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class JEIPlugin implements IModPlugin
{
    private static final ResourceLocation ID = new ResourceLocation(BrewinAndChewin.MODID, "jei_plugin");

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new FermentingRecipeCategory(registry.getJeiHelpers().getGuiHelper(), registry.getJeiHelpers().getModIdHelper()));
        registry.addRecipeCategories(new CheeseRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        BnCJEIRecipes modRecipes = new BnCJEIRecipes();
        registration.addRecipes(BnCJEIRecipeTypes.FERMENTING, modRecipes.getKegRecipes());
        registration.addRecipes(BnCJEIRecipeTypes.AGING, modRecipes.getCheeseRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BnCItems.KEG.get()), BnCJEIRecipeTypes.FERMENTING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
       IIngredientHelper<FluidStack> test = registration.getJeiHelpers().getIngredientManager().getIngredientHelper(ForgeTypes.FLUID_STACK);

       registration.addRecipeClickArea(KegScreen.class, 80, 25, 23, 17, BnCJEIRecipeTypes.FERMENTING);

       Rect2i bounds = new Rect2i(107, 18, 26, 30);

       registration.addGuiContainerHandler(KegScreen.class, new IGuiContainerHandler<>() {
          @Override
          public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse( KegScreen containerScreen, double mouseX, double mouseY ) {
             if ( bounds.contains((int) mouseX - containerScreen.getGuiLeft(), (int) mouseY - containerScreen.getGuiTop()) ) {
                return Optional.of(new IClickableIngredient<FluidStack>() {
                   @Override
                   public ITypedIngredient<FluidStack> getTypedIngredient() {
                      Optional<ITypedIngredient<FluidStack>> aef = registration.getJeiHelpers().getIngredientManager().createTypedIngredient(ForgeTypes.FLUID_STACK, containerScreen.getMenu().blockEntity.getFluidTank().getFluid());
                      return aef.orElse(null);
                   }

                   @Override
                   public Rect2i getArea() {
                      return bounds;
                   }
                });
             }
             return Optional.empty();
          }
       });
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
       registration.addRecipeTransferHandler(new FermentingTransfer.Handler(registration.getTransferHelper(), registration.getJeiHelpers().getStackHelper()), BnCJEIRecipeTypes.FERMENTING);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }
}
