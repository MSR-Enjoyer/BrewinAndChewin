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
import umpaz.farmersrespite.common.registry.FRFluids;
import umpaz.farmersrespite.common.registry.FRItems;
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
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/create/potion"));
        KegPouringRecipeBuilder.kegPouringRecipe(AllFluids.TEA.get().getSource(), 250, AllItems.BUILDERS_TEA.get())
                .withContainer(Items.GLASS_BOTTLE)
                .withCondition(new ModLoadedCondition("create"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/create/builders_tea"));

        // TODO: Move to Farmer's Respite when it updates.
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.GREEN_TEA.get(), 250, FRItems.GREEN_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/green_tea"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.LONG_GREEN_TEA.get(), 250, FRItems.LONG_GREEN_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/long_green_tea"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.STRONG_GREEN_TEA.get(), 250, FRItems.STRONG_GREEN_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/strong_green_tea"));

        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.YELLOW_TEA.get(), 250, FRItems.YELLOW_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/yellow_tea"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.LONG_YELLOW_TEA.get(), 250, FRItems.LONG_YELLOW_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/long_yellow_tea"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.STRONG_YELLOW_TEA.get(), 250, FRItems.STRONG_YELLOW_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/strong_yellow_tea"));

        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.BLACK_TEA.get(), 250, FRItems.BLACK_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/black_tea"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.LONG_BLACK_TEA.get(), 250, FRItems.LONG_BLACK_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/long_black_tea"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.STRONG_BLACK_TEA.get(), 250, FRItems.STRONG_BLACK_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/strong_black_tea"));

        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.ROSE_HIP_TEA.get(), 250, FRItems.ROSE_HIP_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/rose_hip_tea"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.LONG_ROSE_HIP_TEA.get(), 250, FRItems.LONG_ROSE_HIP_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/long_rose_hip_tea"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.STRONG_ROSE_HIP_TEA.get(), 250, FRItems.STRONG_ROSE_HIP_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/strong_rose_hip_tea"));

        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.DANDELION_TEA.get(), 250, FRItems.DANDELION_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/dandelion_tea"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.LONG_DANDELION_TEA.get(), 250, FRItems.LONG_DANDELION_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/long_dandelion_tea"));

        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.PURULENT_TEA.get(), 250, FRItems.PURULENT_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/purulent_tea"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.LONG_PURULENT_TEA.get(), 250, FRItems.LONG_PURULENT_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/long_purulent_tea"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.STRONG_PURULENT_TEA.get(), 250, FRItems.STRONG_PURULENT_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/strong_purulent_tea"));

        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.GAMBLERS_TEA.get(), 250, FRItems.GAMBLERS_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/gamblers_tea"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.LONG_GAMBLERS_TEA.get(), 250, FRItems.LONG_GAMBLERS_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/long_gamblers_tea"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.STRONG_GAMBLERS_TEA.get(), 250, FRItems.STRONG_GAMBLERS_TEA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/strong_gamblers_tea"));

        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.COFFEE.get(), 250, FRItems.COFFEE.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/coffee"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.LONG_COFFEE.get(), 250, FRItems.LONG_COFFEE.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/long_coffee"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.STRONG_COFFEE.get(), 250, FRItems.STRONG_COFFEE.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/strong_coffee"));

        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.APPLE_CIDER.get(), 250, ModItems.APPLE_CIDER.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/apple_cider"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.LONG_APPLE_CIDER.get(), 250, FRItems.LONG_APPLE_CIDER.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/long_apple_cider"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.STRONG_APPLE_CIDER.get(), 250, FRItems.STRONG_APPLE_CIDER.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/strong_apple_cider"));

        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.MELON_JUICE.get(), 250, ModItems.MELON_JUICE.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/melon_juice"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.STRONG_MELON_JUICE.get(), 250, FRItems.STRONG_MELON_JUICE.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/strong_melon_juice"));

        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.HOT_COCOA.get(), 250, ModItems.HOT_COCOA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/hot_cocoa"));
        KegPouringRecipeBuilder.kegPouringRecipe(FRFluids.STRONG_HOT_COCOA.get(), 250, FRItems.STRONG_HOT_COCOA.get())
                .withCondition(new ModLoadedCondition("farmersrespite"))
                .build(consumer, new ResourceLocation(BrewinAndChewin.MODID, "pouring/farmersrespite/strong_hot_cocoa"));
    }
}
