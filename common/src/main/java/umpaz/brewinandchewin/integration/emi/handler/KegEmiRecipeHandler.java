package umpaz.brewinandchewin.integration.emi.handler;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;
import umpaz.brewinandchewin.common.block.entity.container.KegMenu;
import umpaz.brewinandchewin.common.network.serverbound.EMIFillFermentingRecipeServerboundPacket;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.integration.emi.BnCRecipeCategories;
import umpaz.brewinandchewin.integration.emi.recipe.FermentingEmiRecipe;
import umpaz.brewinandchewin.integration.emi.recipe.KegEmiRecipe;
import umpaz.brewinandchewin.integration.emi.recipe.PouringEmiRecipe;
import umpaz.brewinandchewin.integration.emi.widget.BnCFluidWidget;

import java.util.*;
import java.util.function.IntFunction;

/**
 * Code here has been modified from EMI internals.
 * <br>
 * EMI is licensed under the MIT license.
 * <a href="https://github.com/emilyploszaj/emi/blob/1.21/LICENSE">You may read the license here.</a>
 */
public class KegEmiRecipeHandler implements StandardRecipeHandler<KegMenu> {
    private static final Component WARMER_TEMPERATURE = Component.translatable("brewinandchewin.emi.warmer_temperature");
    private static final Component COOLER_TEMPERATURE = Component.translatable("brewinandchewin.emi.cooler_temperature");
    private static final Component CAN_NOT_EMPTY = Component.translatable("brewinandchewin.emi.can_not_empty");

    public List<Slot> getInputSources(KegMenu menu) {
        List<Slot> list = Lists.newArrayList();

        for (int i = 0; i < 4; ++i) {
            list.add(menu.getSlot(i));
        }

        int invStart = 6;

        for (int i = invStart; i < invStart + 36; ++i) {
            list.add(menu.getSlot(i));
        }

        return list;
    }

    public List<Slot> getCraftingSlots(KegMenu menu) {
        List<Slot> list = Lists.newArrayList();

        for (int i = 0; i < 6; ++i) {
            list.add(menu.getSlot(i));
        }

        return list;
    }

    public Slot getOutputSlot(KegMenu menu) {
        return menu.getSlot(KegBlockEntity.OUTPUT_SLOT);
    }

    @Override
    public EmiPlayerInventory getInventory(AbstractContainerScreen<KegMenu> screen) {
        return new EmiPlayerInventory(getInputSources(screen.getMenu()).stream().map(slot -> EmiStack.of(slot.getItem())).toList());
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<KegMenu> context) {
        AbstractedFluidStack stack = context.getScreenHandler().kegTank.getAbstractedFluid();
        EmiStack emiFluid = EmiStack.of(stack.fluid(), stack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY, stack.unit().convertToLoader(context.getAmount()));
        if (recipe instanceof FermentingEmiRecipe fermentingRecipe) {
            if (!KegBlockEntity.isValidTemp(context.getScreenHandler().getKegTemperature(), fermentingRecipe.getTemperature()))
                return false;
            if (!validFluidOrCanEmpty(fermentingRecipe, context))
                return false;
        } else if (recipe instanceof PouringEmiRecipe pouringRecipe) {
            if (!pouringRecipe.getFluidInput().isEmpty() && pouringRecipe.getFluidInput().getEmiStacks().stream().noneMatch(emiStack -> emiStack.isEqual(emiFluid)))
                return false;
        }
        if (recipe instanceof KegEmiRecipe kegRecipe)
            return hasItems(kegRecipe.getItemInputs(), context) && (kegRecipe.getFluidItemInput() == null || hasItems(List.of(kegRecipe.getFluidItemInput()), context));

        return false;
    }

    private boolean hasItems(List<EmiIngredient> ingredients, EmiCraftContext<KegMenu> context) {
        Object2LongMap<EmiStack> used = new Object2LongOpenHashMap<>();

        boolean failure = false;
        root: for (EmiIngredient ingredient : ingredients) {
            if (!ingredient.isEmpty()) {
                for (EmiStack stack : ingredient.getEmiStacks()) {
                    long desired = stack.getAmount() * context.getAmount();
                    if (ingredient.getEmiStacks().getFirst().getKey() instanceof Fluid fluid) {
                        Optional<PouringEmiRecipe> potentialPouring = EmiApi.getRecipeManager().getRecipes(BnCRecipeCategories.POURING).stream().filter(r -> {
                            if (!(r instanceof PouringEmiRecipe pouring))
                                return false;
                            return ingredient.getEmiStacks().stream().anyMatch(es -> es.isEqual(pouring.getFluidInput().getEmiStacks().getFirst())) && r.getInputs().getFirst().getEmiStacks().getFirst().isEqual(stack);
                        }).map(recipe1 -> (PouringEmiRecipe) recipe1).findFirst();
                        if (potentialPouring.isPresent())
                            desired = (context.getScreen().getMenu().kegTank.getFluidCapacity() - context.getScreen().getMenu().kegTank.getAbstractedFluid().amount()) / potentialPouring.get().getFluidInput().getAmount();
                    }
                    if (context.getInventory().inventory.containsKey(stack)) {
                        EmiStack identity = context.getInventory().inventory.get(stack);
                        long alreadyUsed = used.getOrDefault(identity, 0L);
                        long available = identity.getAmount() - alreadyUsed;
                        if (available >= desired) {
                            used.put(identity, desired + alreadyUsed);
                            continue root;
                        }
                    }
                }
                failure = true;
            }
        }

        if (failure) {

        }
        return true;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<KegMenu> context) {
        if (recipe instanceof FermentingEmiRecipe fermentingRecipe) {
            var stacks = BnCEMIRecipeFiller.getFermentingStacks(this, fermentingRecipe, context, context.getAmount());
            if (stacks != null) {
                Minecraft.getInstance().setScreen(context.getScreen());
                AbstractContainerScreen<KegMenu> screen = context.getScreen();

                BrewinAndChewin.getHelper().sendServerbound(new EMIFillFermentingRecipeServerboundPacket(
                        context.getScreenHandler(),
                        stacks
                ));
                return true;
            }
        } else if (recipe instanceof PouringEmiRecipe pouringRecipe) {
            byte destination;
            switch (context.getDestination()) {
                case NONE -> destination = 0;
                case CURSOR -> destination = 1;
                case INVENTORY -> destination = 2;
                default -> throw new MatchException(null, null);
            }
        }
        return false;
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(EmiRecipe recipe, EmiCraftContext<KegMenu> context) {
        List<ClientTooltipComponent> components = Lists.newArrayList();
        components.addAll(StandardRecipeHandler.super.getTooltip(recipe, context));
        if (recipe instanceof FermentingEmiRecipe fermentingRecipe) {
            if (!validFluidOrCanEmpty(fermentingRecipe, context))
                components.add(ClientTooltipComponent.create(CAN_NOT_EMPTY.getVisualOrderText()));

            if (!KegBlockEntity.isValidTemp(context.getScreenHandler().getKegTemperature(), fermentingRecipe.getTemperature())) {
                if (context.getScreenHandler().getKegTemperature() < fermentingRecipe.getTemperature())
                    components.add(ClientTooltipComponent.create(WARMER_TEMPERATURE.getVisualOrderText()));
                else
                    components.add(ClientTooltipComponent.create(COOLER_TEMPERATURE.getVisualOrderText()));
            }
        }

        return components;
    }

    @Override
    public void render(EmiRecipe recipe, EmiCraftContext<KegMenu> context, List<Widget> widgets, GuiGraphics draw) {
        renderMissing(recipe, context, widgets, draw);
    }

    private static void renderMissing(EmiRecipe recipe, EmiCraftContext<KegMenu> context, List<Widget> widgets, GuiGraphics draw) {
        RenderSystem.enableDepthTest();
        Map<EmiIngredient, Boolean> availableForCrafting = getAvailable(recipe, context);

        for(Widget w : widgets) {
            if (w instanceof SlotWidget sw) {
                EmiIngredient stack = sw.getStack();
                Bounds bounds = sw.getBounds();
                if (sw instanceof BnCFluidWidget && recipe instanceof FermentingEmiRecipe fermenting) {
                    if (sw.getRecipe() == null && !validFluidOrCanEmpty(fermenting, context))
                        draw.fill(bounds.x(), bounds.y(), bounds.x() + bounds.width(), bounds.y() + bounds.height(), 1157562368);
                } else if (sw.getRecipe() == null && availableForCrafting.containsKey(stack) && !stack.isEmpty() && !availableForCrafting.get(stack)) {
                    draw.fill(bounds.x(), bounds.y(), bounds.x() + bounds.width(), bounds.y() + bounds.height(), 1157562368);
                }
            }
        }

        if (recipe instanceof FermentingEmiRecipe fermentingRecipe) {
            if (!KegBlockEntity.isValidTemp(context.getScreenHandler().getKegTemperature(), fermentingRecipe.getTemperature()))
                draw.fill(26, 41, 70, 45, 1157562368);
        }
    }

    private static Map<EmiIngredient, Boolean> getAvailable(EmiRecipe recipe, EmiCraftContext<KegMenu> context) {
        Map<EmiIngredient, Boolean> availableForCrafting = new IdentityHashMap<>();
        if (recipe instanceof KegEmiRecipe kegRecipe) {
            List<Boolean> list = getCraftAvailability(kegRecipe, context);
            List<EmiIngredient> inputs = recipe.getInputs();
            if (list.size() != inputs.size()) {
                return Map.of();
            } else {
                for(int i = 0; i < list.size(); ++i) {
                    availableForCrafting.put(inputs.get(i), list.get(i));
                }

                return availableForCrafting;
            }
        }
        return Collections.emptyMap();
    }


    private static boolean validFluidOrCanEmpty(FermentingEmiRecipe recipe, EmiCraftContext<KegMenu> context) {
        boolean success = false;
        Object2LongMap<EmiStack> used = new Object2LongOpenHashMap<>();

        EmiIngredient emptyingIngredient = getEmptyingIngredient(recipe, context);

        if (emptyingIngredient == null)
            return true;

        for (EmiStack stack : emptyingIngredient.getEmiStacks()) {
            long desired = stack.getAmount();
            if (context.getInventory().inventory.containsKey(stack)) {
                EmiStack identity = context.getInventory().inventory.get(stack);
                long alreadyUsed = used.getOrDefault(identity, 0L);
                long available = identity.getAmount() - alreadyUsed;
                if (available >= desired) {
                    used.put(identity, desired + alreadyUsed);
                    success = true;
                    break;
                }
            }
        }

        return success;
    }

    @Nullable
    public static EmiIngredient getEmptyingIngredient(FermentingEmiRecipe fermentingRecipe, EmiCraftContext<KegMenu> context) {
        if (context.getScreenHandler().kegTank.isEmpty() || fermentingRecipe.getFluidInput() == null || fermentingRecipe.getFluidInput().getEmiStacks().stream().anyMatch(emiStack -> {
            AbstractedFluidStack stack = context.getScreenHandler().kegTank.getAbstractedFluid();
            EmiStack tankEmiStack = EmiStack.of(
                    stack.fluid(),
                    stack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY,
                    stack.unit().convertToLoader(stack.amount())
            );
            return emiStack.isEqual(tankEmiStack);
        }))
            return null;

        List<EmiIngredient> ingredients = new ArrayList<>(EmiApi.getRecipeManager().getRecipes(BnCRecipeCategories.POURING).stream()
                .filter(recipe -> {
                    if (!(recipe instanceof PouringEmiRecipe pouringRecipe))
                        return false;
                    AbstractedFluidStack stack = context.getScreenHandler().kegTank.getAbstractedFluid();
                    EmiStack tankEmiStack = EmiStack.of(
                            stack.fluid(),
                            stack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY,
                            stack.unit().convertToLoader(stack.amount())
                    );
                    return pouringRecipe.getFluidInput().getEmiStacks().getFirst().isEqual(tankEmiStack);
                }).map(recipe -> {
                    PouringEmiRecipe pouringRecipe = (PouringEmiRecipe) recipe;
                    return ((PouringEmiRecipe)recipe).getItemInputs().getFirst().copy().setAmount(pouringRecipe.getFluidInput().getAmount());
                }).toList());

        if (ingredients.isEmpty())
            return null;

        return EmiIngredient.of(ingredients);
    }

    private static List<Boolean> getCraftAvailability(KegEmiRecipe recipe, EmiCraftContext<KegMenu> context) {
        Object2LongMap<EmiStack> used = new Object2LongOpenHashMap<>();
        List<Boolean> states = Lists.newArrayList();

        root: for (EmiIngredient ingredient : recipe.getItemInputs()) {
            for (EmiStack stack : ingredient.getEmiStacks()) {
                long desired = stack.getAmount();
                if (context.getInventory().inventory.containsKey(stack)) {
                    EmiStack identity = context.getInventory().inventory.get(stack);
                    long alreadyUsed = used.getOrDefault(identity, 0L);
                    long available = identity.getAmount() - alreadyUsed;
                    if (available >= desired) {
                        used.put(identity, desired + alreadyUsed);
                        states.add(true);
                        continue root;
                    }
                }
            }

            states.add(false);
        }


        if (recipe.getItemInputs() != null && context.getScreenHandler().kegTank.isEmpty()) {
            boolean success = false;
            for (EmiStack stack : recipe.getFluidItemInput().getEmiStacks()) {
                long desired = stack.getAmount();
                if (context.getInventory().inventory.containsKey(stack)) {
                    EmiStack identity = context.getInventory().inventory.get(stack);
                    long alreadyUsed = used.getOrDefault(identity, 0L);
                    long available = identity.getAmount() - alreadyUsed;
                    if (available >= desired) {
                        used.put(identity, desired + alreadyUsed);
                        success = true;
                        break;
                    }
                }
            }
            states.add(success);
        } else if (recipe.getFluidInput() != null) {
            boolean success = false;
            for (EmiStack stack : recipe.getFluidInput().getEmiStacks()) {
                long desired = stack.getAmount();
                AbstractedFluidStack tankStack = context.getScreenHandler().kegTank.getAbstractedFluid();
                EmiStack tankEmiStack = EmiStack.of(tankStack.fluid(), tankStack.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY, desired);
                if (tankStack.amount() >= desired && stack.isEqual(tankEmiStack)) {
                    success = true;
                    break;
                }
            }
            states.add(success);
        }

        return states;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory() == BnCRecipeCategories.FERMENTING && recipe.supportsRecipeTree();
    }

    public enum InputType {
        ITEM,
        FILL,
        EMPTY;

        public static final IntFunction<InputType> BY_ID = ByIdMap.continuous(InputType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, InputType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, InputType::ordinal);
    }
}
