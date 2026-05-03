package umpaz.brewinandchewin.data.builder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.common.utility.FluidUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KegPouringRecipeBuilder {
    private ItemStack container;
    private final Fluid fluid;
    private final int amount;
    private Optional<FluidUnit> unit = Optional.empty();
    private final ItemStack output;
    private final boolean strict;
    private final boolean filling;
    private final List<ICondition> conditions = new ArrayList<>();
    private boolean includeCreateRecipes = true;

    private KegPouringRecipeBuilder(Fluid fluid, int amount, ItemStack output, boolean strict, boolean filling) {
        this.fluid = fluid;
        this.amount = amount;
        this.output = output;
        this.strict = strict;
        this.filling = filling;
    }

    public static KegPouringRecipeBuilder kegPouringRecipe(Fluid fluid, int amount, ItemStack output, boolean strict) {
        return new KegPouringRecipeBuilder(fluid, amount, output, strict, true);
    }

    public static KegPouringRecipeBuilder kegPouringRecipe(Fluid fluid, int amount, ItemStack output, boolean strict, boolean filling) {
        return new KegPouringRecipeBuilder(fluid, amount, output, strict, filling);
    }

    public static KegPouringRecipeBuilder kegPouringRecipe(Fluid fluid, int amount, ItemLike output) {
        return new KegPouringRecipeBuilder(fluid, amount, output.asItem().getDefaultInstance(), false, true);
    }

    public static KegPouringRecipeBuilder kegPouringRecipe(Fluid fluid, int amount, ItemLike output, boolean filling) {
        return new KegPouringRecipeBuilder(fluid, amount, output.asItem().getDefaultInstance(), false, filling);
    }

    /**
     * Used for multi-loader implementation to make sure you can have just the one recipe.
     *
     * @param unit The unit to use for this fluid.
     */
    public KegPouringRecipeBuilder setFluidUnit(FluidUnit unit) {
        this.unit = Optional.of(unit);
        return this;
    }

    public KegPouringRecipeBuilder withContainer(ItemLike container) {
        this.container = container.asItem().getDefaultInstance();
        return this;
    }

    public KegPouringRecipeBuilder withCondition(ICondition condition) {
        conditions.add(condition);
        return this;
    }

    public KegPouringRecipeBuilder excludeCreateCompat() {
        includeCreateRecipes = false;
        return this;
    }

    public void build(RecipeOutput consumerIn) {
        ResourceLocation outputLocation = BuiltInRegistries.ITEM.getKey(output.getItem());
        build(consumerIn, BrewinAndChewin.MODID + ":pouring/" + outputLocation.getPath());
    }

    public void build(RecipeOutput consumerIn, String save) {
        ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(output.getItem());
        if (resourcelocation.equals(ResourceLocation.tryParse(save))) {
            throw new IllegalStateException("Pouring Recipe " + save + " should remove its 'save' argument");
        } else {
            build(consumerIn, ResourceLocation.tryParse(save));
        }
    }

    public void build(RecipeOutput consumerIn, ResourceLocation id) {
        if (!output.hasCraftingRemainingItem() && container == null)
            throw new IllegalStateException("Pouring Recipe " + id + " must specify a container as the output does not have a remainder.");

        consumerIn.accept(id, new KegPouringRecipe(new AbstractedFluidStack(fluid, amount), Optional.ofNullable(container), output, unit, strict, filling), null);

        // TODO: Create recipe compat when Create updates.
//        if (ForgeRegistries.ITEMS.getKey(output.getItem()).getNamespace().equals("create") || !includeCreateRecipes)
//            return;
//
//        var fillingBuilder = new ProcessingRecipeBuilder<>(FillingRecipe::new, new ResourceLocation(BrewinAndChewinNeoForge.MODID, "create/" + id.getPath().replace("pouring/", "")))
//                .require(fluid, amount)
//                .require(container == null ? output.getCraftingRemainingItem().getItem() : container.getItem())
//                .output(output)
//                .withCondition(new ModLoadedCondition("create"));
//
//        for (ICondition condition : conditions)
//            fillingBuilder.withCondition(condition);
//
//        fillingBuilder.build(consumerIn);
//
//        if (!filling)
//            return;
//
//        var emptyingBuilder = new ProcessingRecipeBuilder<>(EmptyingRecipe::new, new ResourceLocation(BrewinAndChewinNeoForge.MODID, "create/" + id.getPath().replace("pouring/", "")))
//                .output(fluid, amount)
//                .output(container == null ? output.getCraftingRemainingItem().getItem() : container.getItem())
//                .withCondition(new ModLoadedCondition("create"));
//
//        if (strict)
//            emptyingBuilder.require(StrictNBTIngredient.of(output));
//        else
//            emptyingBuilder.require(output.getItem());
//
//        for (ICondition condition : conditions)
//            emptyingBuilder.withCondition(condition);
//
//        emptyingBuilder.build(consumerIn);
    }
}
