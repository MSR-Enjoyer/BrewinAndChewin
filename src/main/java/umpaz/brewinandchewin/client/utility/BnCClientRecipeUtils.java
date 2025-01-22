package umpaz.brewinandchewin.client.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;

import java.util.Comparator;
import java.util.Optional;

public class BnCClientRecipeUtils {
    public static ItemStack getPouredItemFromFluid(MinecraftServer server, FluidStack fluid) {
        if (fluid.isEmpty() || Minecraft.getInstance().level == null)
            return ItemStack.EMPTY;
        RegistryAccess registryAccess = (server == null || EffectiveSide.get().isClient()) ? Minecraft.getInstance().level.registryAccess() : server.registryAccess();
        ItemStack itemDisplay = BnCFluidItemDisplays.getFluidItemDisplay(registryAccess, fluid);

        if (!itemDisplay.isEmpty())
            return itemDisplay;

        RecipeManager recipeManager = (server == null || EffectiveSide.get().isClient()) ? Minecraft.getInstance().level.getRecipeManager() : server.getRecipeManager();
        Optional<KegPouringRecipe> recipe = recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream().sorted(Comparator.comparing(KegPouringRecipe::isStrict)).filter(kegPouringRecipe -> kegPouringRecipe.getRawFluid().isSame(fluid.getRawFluid())).findFirst();
        return recipe.map(KegPouringRecipe::getOutput).orElse(ItemStack.EMPTY);
    }
}
