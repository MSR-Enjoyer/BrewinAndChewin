package umpaz.brewinandchewin.integration.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.utility.BnCFluidItemDisplays;
import umpaz.brewinandchewin.common.BnCConfiguration;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.registry.BnCItems;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.BnCTextUtils;
import umpaz.brewinandchewin.integration.jei.BnCJEIRecipeTypes;
import umpaz.brewinandchewin.integration.jei.KegFermentingPouringRecipe;
import vectorwing.farmersdelight.common.utility.ClientRenderUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class FermentingRecipeCategory implements IRecipeCategory<KegFermentingPouringRecipe> {
    public static final ResourceLocation UID = BrewinAndChewin.asResource("fermenting");
    protected final IModIdHelper modIdHelper;

    protected final IDrawableAnimated arrow;
    private final Component title;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable cold;
    private final IDrawable chilly;
    private final IDrawable warm;
    private final IDrawable hot;
    protected final IDrawable timeIcon;
    protected final IDrawable expIcon;
    protected final IDrawable kegOverlay;
    protected final IDrawableAnimated leftBubble;
    protected final IDrawableAnimated rightBubble;


    public FermentingRecipeCategory(IGuiHelper guiHelper, IModIdHelper modIdHelper) {
        this.modIdHelper = modIdHelper;
        title = BnCTextUtils.getTranslation("jei.fermenting");
        ResourceLocation backgroundImage = BrewinAndChewin.asResource("textures/gui/jei/keg.png");
        background = guiHelper.createDrawable(backgroundImage, 12, 13, 136, 56);
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BnCItems.KEG));
        arrow = guiHelper.drawableBuilder(backgroundImage, 171, 4, 23, 16)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
        cold = guiHelper.createDrawable(backgroundImage, 170, 0, 8, 3);
        chilly = guiHelper.createDrawable(backgroundImage, 178, 0, 9, 3);
        warm = guiHelper.createDrawable(backgroundImage, 195, 0, 9, 3);
        hot = guiHelper.createDrawable(backgroundImage, 204, 0, 8, 3);
        expIcon = guiHelper.createDrawable(backgroundImage, 170, 32, 9, 9);
        timeIcon = guiHelper.createDrawable(backgroundImage, 170, 21, 8, 11);
        kegOverlay = guiHelper.createDrawable(backgroundImage, 170, 45, 26, 30);
        leftBubble = guiHelper.drawableBuilder(backgroundImage, 170, 75, 9, 24)
                .buildAnimated(50, IDrawableAnimated.StartDirection.BOTTOM, false);
        rightBubble = guiHelper.drawableBuilder(backgroundImage, 180, 75, 9, 24)
                .buildAnimated(50, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    public RecipeType<KegFermentingPouringRecipe> getRecipeType() {
        return BnCJEIRecipeTypes.FERMENTING;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, KegFermentingPouringRecipe recipe, IFocusGroup focusGroup) {
        NonNullList<Ingredient> recipeIngredients = recipe.getIngredients();

        int borderSlotSize = 18;
        for (int row = 0; row < 2; ++row) {
            for (int column = 0; column < 2; ++column) {
                int inputIndex = row * 2 + column;
                if (inputIndex < recipeIngredients.size()) {
                    builder.addSlot(RecipeIngredientRole.INPUT, (column * borderSlotSize) + 29, (row * borderSlotSize) + 1)
                            .addItemStacks(Arrays.asList(recipeIngredients.get(inputIndex).getItems()));
                }
            }
        }


        if (recipe.getFluidIngredient().isPresent()) {
            var firstDisplayFluid = recipe.getFluidIngredient().get().ingredient().displayStacks().getFirst();
            if (BnCConfiguration.CLIENT_CONFIG.get().renderFluidInKeg()) {
                builder.addSlot(RecipeIngredientRole.INPUT, 0, 2)
                        .addFluidStack(firstDisplayFluid.fluid(), recipe.getFluidIngredient().get().amount(), firstDisplayFluid.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY)
                        .setFluidRenderer(BnCConfiguration.COMMON_CONFIG.get().keg().localizedCapacity(), false, 26, 30)
                        .setOverlay(kegOverlay, 0, 0);
            } else
                builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                        .addFluidStack(firstDisplayFluid.fluid(), recipe.getFluidIngredient().get().amount(), firstDisplayFluid.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY);

            ItemStack itemDisplay = BnCFluidItemDisplays.getFluidItemDisplay(Minecraft.getInstance().level.registryAccess(), firstDisplayFluid).copy();
            Optional<KegPouringRecipe> pouringRecipe = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(BnCRecipeTypes.KEG_POURING).stream().map(RecipeHolder::value).sorted(Comparator.comparing(KegPouringRecipe::isStrict)).filter(kegPouringRecipe -> {
                    if (kegPouringRecipe.isStrict())
                        return ItemStack.isSameItemSameComponents(itemDisplay, kegPouringRecipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
                    return ItemStack.isSameItem(itemDisplay, kegPouringRecipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
            }).findFirst();
            int pourCount = pouringRecipe.map(kegPouringRecipe -> (int)(Math.min(BnCConfiguration.COMMON_CONFIG.get().keg().localizedCapacity(), recipe.getFluidIngredient().get().loaderAmount()) / kegPouringRecipe.getLoaderAmount())).orElse(1);
            itemDisplay.setCount(pourCount);
            if (!itemDisplay.isEmpty())
                builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 5, 5)
                        .addItemStack(itemDisplay);
        }

        if (recipe.getResult().left().isPresent()) {
            var result = recipe.getResult().left().get();
            if (BnCConfiguration.CLIENT_CONFIG.get().renderFluidInKeg()) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 2)
                        .addFluidStack(result.fluid(), recipe.getUnit().convertToLoader(result.amount()), result.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY)
                        .setFluidRenderer(BnCConfiguration.COMMON_CONFIG.get().keg().localizedCapacity(), false, 26, 30)
                        .setOverlay(kegOverlay, 0, 0);
            } else
                builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
                        .addFluidStack(result.fluid(), recipe.getUnit().convertToLoader(result.amount()), result.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY);

            ItemStack itemDisplay = BnCFluidItemDisplays.getFluidItemDisplay(Minecraft.getInstance().level.registryAccess(), recipe.getResult().left().get()).copy();
            int pourCount = recipe.getPouringLoaderAmount() == -1L ? 1 : (int)(Math.min(BnCConfiguration.COMMON_CONFIG.get().keg().localizedCapacity(), recipe.getFluidIngredient().get().loaderAmount()) / recipe.getPouringLoaderAmount());
            itemDisplay.setCount(pourCount);
            if (!itemDisplay.isEmpty())
                builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 105, 5)
                        .addItemStack(itemDisplay);
        }

        if (recipe.getCatalyst() != null) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 73, 39).addItemStack(recipe.getCatalyst());
        }
        if (recipe.getOutput() != null) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 105, 39).addItemStack(recipe.getOutput());
        }

        builder.moveRecipeTransferButton(132, 43);
    }


    @Override
    public void onDisplayedIngredientsUpdate(KegFermentingPouringRecipe recipe, List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
        if (recipe.getFluidIngredient().isPresent() && recipe.getFluidIngredient().get().ingredient().displayStacks().size() > 1) {
            List<AbstractedFluidStack> stacks = recipe.getFluidIngredient().get().ingredient().displayStacks();
            AbstractedFluidStack fluidInput = stacks.get(new Random().nextInt(stacks.size()));

            var drawable = recipeSlots.getFirst(); // Fluid stack.
            drawable.clearDisplayOverrides();
            drawable.createDisplayOverrides().addFluidStack(fluidInput.fluid(), fluidInput.unit().convertToLoader(fluidInput.amount()), fluidInput.components() instanceof PatchedDataComponentMap patched ? patched.asPatch() : DataComponentPatch.EMPTY);

            ItemStack itemDisplay = BnCFluidItemDisplays.getFluidItemDisplay(Minecraft.getInstance().level.registryAccess(), fluidInput).copy();

            int pourCount = recipe.getPouringLoaderAmount() == -1L ? 1 : (int) (Math.min(BnCConfiguration.COMMON_CONFIG.get().keg().localizedCapacity(), recipe.getUnit().convertToLoader(recipe.getFluidIngredient().get().amount())) / recipe.getPouringLoaderAmount());
            itemDisplay.setCount(pourCount);
            if (!itemDisplay.isEmpty()) {
                var itemDisplayDrawable = recipeSlots.get(1);
                itemDisplayDrawable.clearDisplayOverrides();
                itemDisplayDrawable.createDisplayOverrides().addItemStack(itemDisplay);
            }
        }
    }

    @Override
    public void draw(KegFermentingPouringRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 67, 10);
        leftBubble.draw(guiGraphics, 90, 3);
        rightBubble.draw(guiGraphics, 127, 3);

        if (recipe.getTemperature() <= 2) {
            chilly.draw(guiGraphics, 33, 39);
        }
        if (recipe.getTemperature() <= 1) {
            cold.draw(guiGraphics, 25, 39);
        }
        if (recipe.getTemperature() >= 4) {
            warm.draw(guiGraphics, 50, 39);
        }
        if (recipe.getTemperature() >= 5) {
            hot.draw(guiGraphics, 59, 39);
        }

        timeIcon.draw(guiGraphics, 70, 2);
        if (recipe.getExperience() > 0) {
            expIcon.draw(guiGraphics, 69, 21);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, KegFermentingPouringRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (ClientRenderUtils.isCursorInsideBounds(67, 2, 22, 28, mouseX, mouseY)) {
            int cookTime = recipe.getFermentTime();
            if (cookTime > 0) {
                if (cookTime >= 1200)
                    tooltip.add(Component.translatable("gui.jei.category.smelting.time.minutes", cookTime / 1200));
                else
                    tooltip.add(Component.translatable("gui.jei.category.smelting.time.seconds", cookTime / 20));
            }
            float experience = recipe.getExperience();
            if (experience > 0) {
                tooltip.add(Component.translatable("gui.jei.category.smelting.experience", experience));
            }
        } else if (ClientRenderUtils.isCursorInsideBounds(24, 38, 44, 5, mouseX, mouseY)) {
            MutableComponent key = switch (recipe.getTemperature()) {
                case 1 -> BnCTextUtils.getTranslation("container.keg.cold");
                case 2 -> BnCTextUtils.getTranslation("container.keg.chilly");
                case 3 -> BnCTextUtils.getTranslation("container.keg.normal");
                case 4 -> BnCTextUtils.getTranslation("container.keg.warm");
                case 5 -> BnCTextUtils.getTranslation("container.keg.hot");
                default -> null;
            };
            if (key != null)
                tooltip.add(key);
        } else if (ClientRenderUtils.isCursorInsideBounds(92, 39, 10, 16, mouseX, mouseY)) {
            if (recipe.getCatalyst() != null) {
                tooltip.add(Component.literal(String.valueOf(recipe.getCatalystAmount())).append(I18n.get("generic.unit.millibuckets")));
            }
        }
    }

}
