package umpaz.brewinandchewin.common.utility;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.server.ServerLifecycleHooks;
import umpaz.brewinandchewin.client.utility.BnCClientRecipeUtils;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;

import java.util.Comparator;
import java.util.Optional;

public class BnCRecipeUtils {
    public static ItemStack getPouredItemFromFluid(FluidStack fluid) {
        if (fluid.isEmpty())
            return ItemStack.EMPTY;
        if (SideUtil.isClient())
            return BnCClientRecipeUtils.getPouredItemFromFluid(fluid);
        Optional<KegPouringRecipe> recipe = ServerLifecycleHooks.getCurrentServer().getRecipeManager().getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get()).stream().sorted(Comparator.comparing(KegPouringRecipe::isStrict)).filter(kegPouringRecipe -> kegPouringRecipe.getRawFluid().isSame(fluid.getRawFluid())).findFirst();
        return recipe.map(KegPouringRecipe::getOutput).orElse(ItemStack.EMPTY);
    }
}
