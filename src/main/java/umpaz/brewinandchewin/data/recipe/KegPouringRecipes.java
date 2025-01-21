package umpaz.brewinandchewin.data.recipe;

import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.registry.BnCFluids;
import umpaz.brewinandchewin.common.registry.BnCItems;
import umpaz.brewinandchewin.data.builder.CreatePotionPouringRecipeBuilder;
import umpaz.brewinandchewin.data.builder.KegPouringRecipeBuilder;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.function.Consumer;

public class KegPouringRecipes {

    public static void register(Consumer<FinishedRecipe> consumer) {
        cookMiscellaneous(consumer);
    }

    private static void cookMiscellaneous(Consumer<FinishedRecipe> consumer) {
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.BEER.get(), 250, BnCItems.BEER.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.VODKA.get(), 250, BnCItems.VODKA.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.MEAD.get(), 250, BnCItems.MEAD.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.RICE_WINE.get(), 250, BnCItems.RICE_WINE.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.EGG_GROG.get(), 250, BnCItems.EGG_GROG.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.STRONGROOT_ALE.get(), 250, BnCItems.STRONGROOT_ALE.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.SACCHARINE_RUM.get(), 250, BnCItems.SACCHARINE_RUM.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.PALE_JANE.get(), 250, BnCItems.PALE_JANE.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.SALTY_FOLLY.get(), 250, BnCItems.SALTY_FOLLY.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.STEEL_TOE_STOUT.get(), 250, BnCItems.STEEL_TOE_STOUT.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.GLITTERING_GRENADINE.get(), 250, BnCItems.GLITTERING_GRENADINE.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.BLOODY_MARY.get(), 250, BnCItems.BLOODY_MARY.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.RED_RUM.get(), 250, BnCItems.RED_RUM.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.WITHERING_DROSS.get(), 250, BnCItems.WITHERING_DROSS.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.DREAD_NOG.get(), 250, BnCItems.DREAD_NOG.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.KOMBUCHA.get(), 250, BnCItems.KOMBUCHA.get())
                .build(consumer);

        KegPouringRecipeBuilder.kegPouringRecipe(ForgeMod.MILK.get(), 250, ModItems.MILK_BOTTLE.get())
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(Fluids.WATER, 250, Items.POTION.getDefaultInstance(), true)
                .withContainer(Items.GLASS_BOTTLE)
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.HONEY_FLUID.get(), 250, Items.HONEY_BOTTLE)
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(Fluids.WATER, 1000, Items.WATER_BUCKET)
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(ForgeMod.MILK.get(), 1000, Items.MILK_BUCKET)
                .build(consumer);

        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.FLAXEN_CHEESE.get(), 1000, BnCItems.UNRIPE_FLAXEN_CHEESE_WHEEL.get(), false)
                .withContainer(Items.HONEYCOMB)
                .build(consumer);
        KegPouringRecipeBuilder.kegPouringRecipe(BnCFluids.SCARLET_CHEESE.get(), 1000, BnCItems.UNRIPE_SCARLET_CHEESE_WHEEL.get(), false)
                .withContainer(Items.HONEYCOMB)
                .build(consumer);

        // Create Compat
        CreatePotionPouringRecipeBuilder.createPotionPouringRecipe(Items.GLASS_BOTTLE, 250)
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/create_potion"));
        KegPouringRecipeBuilder.kegPouringRecipe(AllFluids.TEA.get().getSource(), 250, AllItems.BUILDERS_TEA.get())
                .withContainer(Items.GLASS_BOTTLE)
                .withCondition(new ModLoadedCondition("create"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/create_builders_tea"));
    }
}
