package umpaz.brewinandchewin.integration.emi.widget;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.level.material.Fluid;
import umpaz.brewinandchewin.client.BrewinAndChewinClient;
import umpaz.brewinandchewin.common.BnCConfiguration;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.FluidUnit;
import umpaz.brewinandchewin.integration.emi.recipe.FermentingEmiRecipe;

public class BnCFluidWidget extends SlotWidget {
    private final EmiIngredient item;
    private final AbstractedFluidStack fluidStack;

    public BnCFluidWidget(EmiIngredient fluid, EmiIngredient item, int x, int y) {
        super(fluid, x, y);
        this.item = item;
        EmiStack fluidIngredient = fluid.getEmiStacks().getFirst();
        fluidStack = new AbstractedFluidStack((Fluid) fluidIngredient.getKey(), fluidIngredient.getAmount(), PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, fluidIngredient.getComponentChanges()), FluidUnit.MILLIBUCKETS, null);
        custom = true;
        customWidth = 28;
        customHeight = 32;
        output = true;
    }

    public void drawBackground(GuiGraphics gui, int mouseX, int mouseY, float delta) {}

    @Override
    public void drawStack(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        Bounds bounds = this.getBounds();
        if (BnCConfiguration.CLIENT_CONFIG.get().renderFluidInKeg())
            BrewinAndChewinClient.getHelper().renderFluidInKeg(fluidStack, gui, bounds.x() + 2, bounds.y() + 2, 1.0F);
        int xOff = (bounds.width() - 16) / 2;
        int yOff = (bounds.height() - 16) / 2 - 4;
        item.render(gui, bounds.x() + xOff, bounds.y() + yOff, delta);
    }

    @Override
    public void drawOverlay(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        Bounds bounds = this.getBounds();
        gui.blit(FermentingEmiRecipe.BACKGROUND, bounds.x() + 1, bounds.y() + 1, 170, 45, bounds.width(), bounds.height() - 2);
        super.drawOverlay(gui, mouseX, mouseY, delta);
    }
}
