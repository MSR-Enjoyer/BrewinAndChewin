package umpaz.brewinandchewin.integration.emi.handler;

import com.google.common.collect.Lists;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import umpaz.brewinandchewin.common.block.entity.container.KegMenu;
import umpaz.brewinandchewin.integration.emi.BnCRecipeCategories;

import java.util.List;

public class KegEmiRecipeHandler implements EmiRecipeHandler<KegMenu> {
    private List<Slot> getInputSources(KegMenu handler) {
        List<Slot> list = Lists.newArrayList();

        for(int i = 1; i < 5; ++i) {
            list.add(handler.getSlot(i));
        }

        int invStart = 6;

        for(int i = invStart; i < invStart + 36; ++i) {
            list.add(handler.getSlot(i));
        }

        return list;
    }

    @Override
    public EmiPlayerInventory getInventory(AbstractContainerScreen<KegMenu> screen) {
        return new EmiPlayerInventory(getInputSources(screen.getMenu()).stream().map(slot -> EmiStack.of(slot.getItem())).toList());
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<KegMenu> context) {
        return context.getInventory().canCraft(recipe);
    }

    // TODO: Handle craft.
    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<KegMenu> context) {
//        List<ItemStack> stacks = EmiRecipeFiller.getStacks(this, recipe, context.getScreen(), context.getAmount());
//        if (stacks != null) {
//            Minecraft.getInstance().setScreen(context.getScreen());
//            AbstractContainerScreen<KegMenu> screen = context.getScreen();
//            int containerId = context.getScreenHandler().containerId;
//            byte destination;
//            switch (context.getDestination()) {
//                case NONE -> destination = 0;
//                case CURSOR -> destination = 1;
//                case INVENTORY -> destination = 2;
//                default -> throw new MatchException(null, null);
//            }
//
//            BrewinAndChewin.getHelper().sendServerbound();
//            return true;
//        }
        return false;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory() == BnCRecipeCategories.FERMENTING && recipe.supportsRecipeTree();
    }
}
