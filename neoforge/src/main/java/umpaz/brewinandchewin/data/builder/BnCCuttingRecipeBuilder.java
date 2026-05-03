package umpaz.brewinandchewin.data.builder;

import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.crafting.ingredient.ChanceResult;

import java.util.Optional;

public class BnCCuttingRecipeBuilder {
    private final NonNullList<ChanceResult> results = NonNullList.createWithCapacity(4);
    private final Ingredient ingredient;
    private final Ingredient tool;
    private Optional<SoundEvent> soundEvent = Optional.empty();

    private BnCCuttingRecipeBuilder(Ingredient ingredient, Ingredient tool, ItemLike mainResult, int count, float chance) {
        this.results.add(new ChanceResult(new ItemStack(mainResult.asItem(), count), chance));
        this.ingredient = ingredient;
        this.tool = tool;
    }

    public static BnCCuttingRecipeBuilder cuttingRecipe(Ingredient ingredient, Ingredient tool, ItemLike mainResult, int count) {
        return new BnCCuttingRecipeBuilder(ingredient, tool, mainResult, count, 1.0F);
    }

    public static BnCCuttingRecipeBuilder cuttingRecipe(Ingredient ingredient, Ingredient tool, ItemLike mainResult, int count, int chance) {
        return new BnCCuttingRecipeBuilder(ingredient, tool, mainResult, count, (float)chance);
    }

    public static BnCCuttingRecipeBuilder cuttingRecipe(Ingredient ingredient, Ingredient tool, ItemLike mainResult) {
        return new BnCCuttingRecipeBuilder(ingredient, tool, mainResult, 1, 1.0F);
    }

    public BnCCuttingRecipeBuilder addResult(ItemLike result) {
        return this.addResult(result, 1);
    }

    public BnCCuttingRecipeBuilder addResult(ItemLike result, int count) {
        this.results.add(new ChanceResult(new ItemStack(result.asItem(), count), 1.0F));
        return this;
    }

    public BnCCuttingRecipeBuilder addResultWithChance(ItemLike result, float chance) {
        return this.addResultWithChance(result, chance, 1);
    }

    public BnCCuttingRecipeBuilder addResultWithChance(ItemLike result, float chance, int count) {
        this.results.add(new ChanceResult(new ItemStack(result.asItem(), count), chance));
        return this;
    }

    public BnCCuttingRecipeBuilder addSound(SoundEvent soundEvent) {
        this.soundEvent = Optional.of(soundEvent);
        return this;
    }

    public void build(RecipeOutput consumerIn) {
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(this.ingredient.getItems()[0].getItem());
        this.build(consumerIn, "brewinandchewin:cutting/" + location.getPath());
    }

    public void build(RecipeOutput consumerIn, String save) {
        ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(this.ingredient.getItems()[0].getItem());
        ResourceLocation parsed = ResourceLocation.tryParse(save);
        if (parsed != null && parsed.equals(resourcelocation)) {
            throw new IllegalStateException("Cutting Recipe " + save + " should remove its 'save' argument");
        } else {
            this.build(consumerIn, parsed);
        }
    }

    public void build(RecipeOutput consumerIn, ResourceLocation id) {
        consumerIn.accept(id, new CuttingBoardRecipe("", this.ingredient, this.tool, this.results, this.soundEvent), null);
    }
}