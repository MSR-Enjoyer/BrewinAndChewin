package umpaz.brewinandchewin.integration.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.utility.BnCFluidItemDisplays;
import umpaz.brewinandchewin.common.BnCConfiguration;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.BnCTextUtils;
import umpaz.brewinandchewin.common.utility.FluidUnit;
import umpaz.brewinandchewin.integration.emi.BnCRecipeCategories;
import umpaz.brewinandchewin.integration.emi.widget.BnCFluidWidget;
import vectorwing.farmersdelight.common.utility.ClientRenderUtils;

import java.util.*;

public class FermentingEmiRecipe implements EmiRecipe {
    public static final ResourceLocation BACKGROUND = BrewinAndChewin.asResource("textures/gui/jei/keg.png");

    private final ResourceLocation id;
    private final List<EmiIngredient> itemInputs;
    @Nullable
    private final EmiIngredient fluidInput;
    @Nullable
    private final EmiStack fluidOutput;
    private final EmiStack itemOutput;
    private final EmiStack container;
    private final int temperature;
    private final int cookTime;
    private final float experience;

    private List<EmiIngredient> inputs;
    private List<EmiIngredient> catalysts;
    private List<EmiStack> outputs;

    public FermentingEmiRecipe(ResourceLocation id, List<EmiIngredient> itemInputs,
                               @Nullable EmiIngredient fluidInput, @Nullable EmiStack fluidOutput,
                               EmiStack itemOutput,
                               EmiStack container,
                               int temperature,
                               int cookTime, float experience) {
        this.id = id;
        this.itemInputs = itemInputs;
        this.fluidInput = fluidInput;
        this.fluidOutput = fluidOutput;
        this.itemOutput = itemOutput;
        this.container = container;
        this.temperature = temperature;
        this.cookTime = cookTime;
        this.experience = experience;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return BnCRecipeCategories.FERMENTING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        if (inputs == null) {
            List<EmiIngredient> ingredients = new ArrayList<>(itemInputs);
            if (fluidInput != null)
                ingredients.add(fluidInput);
            inputs = List.copyOf(ingredients);
        }
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        if (outputs == null) {
            List<EmiStack> stacks = new ArrayList<>();
            stacks.add(itemOutput);
            if (fluidOutput != null)
                stacks.add(fluidOutput);
            outputs = List.copyOf(stacks);
        }
        return outputs;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        if (catalysts == null) {
            List<EmiIngredient> stacks = new ArrayList<>();
            if (container != null)
                stacks.add(container);
            catalysts = List.copyOf(stacks);
        }
        return catalysts;
    }

    @Override
    public int getDisplayWidth() {
        return 136;
    }

    @Override
    public int getDisplayHeight() {
        return 60;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(BACKGROUND, 0, 0, 138, 58, 10, 11);

        int borderSlotSize = 18;
        for (int row = 0; row < 2; ++row) {
            for (int column = 0; column < 2; ++column) {
                int inputIndex = row * 2 + column;
                if (inputIndex < itemInputs.size()) {
                    addSlot(widgets, itemInputs.get(inputIndex), (column * borderSlotSize) + 30, (row * borderSlotSize) + 2);
                }
            }
        }

        if (fluidInput != null) {
            AbstractedFluidStack fluidStack = new AbstractedFluidStack((Fluid) fluidInput.getEmiStacks().getFirst().getKey(), fluidInput.getEmiStacks().getFirst().getAmount(), PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, fluidInput.getEmiStacks().getFirst().getComponentChanges()), FluidUnit.getLoaderUnit());
            ItemStack itemDisplay = BnCFluidItemDisplays.getFluidItemDisplay(Minecraft.getInstance().level.registryAccess(), fluidStack).copy();
            Optional<KegPouringRecipe> pouringRecipe = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(BnCRecipeTypes.KEG_POURING).stream().map(RecipeHolder::value).sorted(Comparator.comparing(KegPouringRecipe::isStrict)).filter(kegPouringRecipe -> {
                if (kegPouringRecipe.isStrict())
                    return ItemStack.isSameItemSameComponents(itemDisplay, kegPouringRecipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
                return ItemStack.isSameItem(itemDisplay, kegPouringRecipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
            }).findFirst();
            int pourCount = pouringRecipe.map(kegPouringRecipe -> (int)(Math.min(FluidUnit.convert(BnCConfiguration.COMMON_CONFIG.get().keg().capacity(), BnCConfiguration.COMMON_CONFIG.get().keg().capacityUnit(), FluidUnit.MILLIBUCKETS), fluidInput.getAmount()) / kegPouringRecipe.getRawFluid().amount())).orElse(1);
            itemDisplay.setCount(pourCount);
            if (!itemDisplay.isEmpty())
                widgets.add(new BnCFluidWidget(fluidInput, EmiStack.of(itemDisplay), 1, 3));
        }

        if (fluidOutput != null) {
            AbstractedFluidStack fluidStack = new AbstractedFluidStack((Fluid) fluidOutput.getKey(), fluidOutput.getAmount(), PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, fluidOutput.getComponentChanges()), FluidUnit.getLoaderUnit());
            ItemStack itemDisplay = BnCFluidItemDisplays.getFluidItemDisplay(Minecraft.getInstance().level.registryAccess(), fluidStack).copy();
            Optional<KegPouringRecipe> pouringRecipe = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(BnCRecipeTypes.KEG_POURING).stream().map(RecipeHolder::value).sorted(Comparator.comparing(KegPouringRecipe::isStrict)).filter(kegPouringRecipe -> {
                if (kegPouringRecipe.isStrict())
                    return ItemStack.isSameItemSameComponents(itemDisplay, kegPouringRecipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
                return ItemStack.isSameItem(itemDisplay, kegPouringRecipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
            }).findFirst();
            int pourCount = pouringRecipe.map(kegPouringRecipe -> (int)(Math.min(FluidUnit.convert(BnCConfiguration.COMMON_CONFIG.get().keg().capacity(), BnCConfiguration.COMMON_CONFIG.get().keg().capacityUnit(), FluidUnit.MILLIBUCKETS), fluidInput.getAmount()) / kegPouringRecipe.getRawFluid().amount())).orElse(1);
            itemDisplay.setCount(pourCount);
            if (!itemDisplay.isEmpty())
                widgets.add(new BnCFluidWidget(fluidOutput, EmiStack.of(itemDisplay), 101, 3)).recipeContext(this);
        }

        if (container != null)
            addSlot(widgets, container, 74, 40);

        addSlot(widgets, itemOutput, 106, 40).recipeContext(this);

        // Arrow
        widgets.addAnimatedTexture(BACKGROUND, 69, 12, 23, 16, 171, 4, 1000 * 10, true, false, false);

        if (temperature <= 2) {
            // Chilly
            widgets.addTexture(BACKGROUND, 35, 41, 9, 3, 178, 0);
        }
        if (temperature <= 1) {
            // Cold
            widgets.addTexture(BACKGROUND, 27, 41, 8, 3, 170, 0);
        }
        if (temperature >= 4) {
            // Warm
            widgets.addTexture(BACKGROUND, 52, 41, 9, 3, 195, 0);
        }
        if (temperature >= 5) {
            // Hot
            widgets.addTexture(BACKGROUND, 61, 41, 8, 3, 204, 0);
        }

        // Time Icon
        widgets.addTexture(BACKGROUND, 72, 4, 8, 11, 170, 21);
        // Experience Icon
        if (experience > 0) {
            widgets.addTexture(BACKGROUND, 71, 23, 9, 9, 170, 32);
        }

        widgets.addTooltip(this::getTooltips, 0, 0, widgets.getWidth(), widgets.getHeight());
    }

    private SlotWidget addSlot(WidgetHolder widgets, EmiIngredient ingredient, int x, int y) {
        return widgets.addSlot(ingredient, x, y).drawBack(false);
    }

    public List<ClientTooltipComponent> getTooltips(double mouseX, double mouseY) {
        List<ClientTooltipComponent> tooltip = new ArrayList<>();
        if (ClientRenderUtils.isCursorInsideBounds(68, 2, 22, 28, mouseX, mouseY)) {
            if (cookTime > 0) {
                tooltip.add(ClientTooltipComponent.create(Component.translatable("emi.cooking.time", cookTime / 20F).getVisualOrderText()));
            }
            if (experience > 0) {
                tooltip.add(ClientTooltipComponent.create(Component.translatable("emi.cooking.experience", experience).getVisualOrderText()));
            }
        } else if (ClientRenderUtils.isCursorInsideBounds(26, 41, 44, 5, mouseX, mouseY)) {
            MutableComponent key = switch (temperature) {
                case 1 -> BnCTextUtils.getTranslation("container.keg.cold");
                case 2 -> BnCTextUtils.getTranslation("container.keg.chilly");
                case 3 -> BnCTextUtils.getTranslation("container.keg.normal");
                case 4 -> BnCTextUtils.getTranslation("container.keg.warm");
                case 5 -> BnCTextUtils.getTranslation("container.keg.hot");
                default -> null;
            };
            if (key != null)
                tooltip.add(ClientTooltipComponent.create(key.getVisualOrderText()));
        }
        return tooltip;
    }
}
