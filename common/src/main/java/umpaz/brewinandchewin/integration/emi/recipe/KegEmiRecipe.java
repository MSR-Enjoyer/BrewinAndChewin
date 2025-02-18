package umpaz.brewinandchewin.integration.emi.recipe;

import dev.emi.emi.api.stack.EmiIngredient;

import java.util.List;

public interface KegEmiRecipe {
    List<EmiIngredient> getItemInputs();
    EmiIngredient getFluidInput();
    EmiIngredient getFluidItemInput();
}
