package umpaz.brewinandchewin.data.recipe;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;
import umpaz.brewinandchewin.client.recipebook.FermentingRecipeBookTab;
import umpaz.brewinandchewin.common.registry.BnCFluids;
import umpaz.brewinandchewin.common.registry.BnCItems;
import umpaz.brewinandchewin.common.tag.BnCTags;
import umpaz.brewinandchewin.data.builder.KegFermentingRecipeBuilder;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.ForgeTags;

import java.util.function.Consumer;

public class KegFermentingRecipes {
   public static final int NORMAL_COOKING = 4800;    // 4 minutes
   public static final int SLOW_COOKING = 9600;      // 8 minutes


   public static final float MEDIUM_EXP = 1.0F;
   public static final float LARGE_EXP = 2.0F;

   public static void register( Consumer<FinishedRecipe> consumer ) {
      cookMiscellaneous(consumer);
   }

   private static void cookMiscellaneous( Consumer<FinishedRecipe> consumer ) {
      ITagManager<Item> tags = ForgeRegistries.ITEMS.tags();
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.BEER.get(), 1000, SLOW_COOKING, MEDIUM_EXP)
              .addFluidIngredient(Fluids.WATER, 1000)
              .addIngredient(Items.WHEAT)
              .addIngredient(Items.WHEAT_SEEDS)
              .addIngredient(Items.BROWN_MUSHROOM)
              .setRecipeBookTab(FermentingRecipeBookTab.DRINKS)
              .unlockedByItems("has_tankard", BnCItems.TANKARD.get())
              .unlockedByItems("has_wheat", Items.WHEAT)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.VODKA.get(), 1000, SLOW_COOKING, MEDIUM_EXP)
              .addFluidIngredient(Fluids.WATER, 1000)
              .addIngredient(ForgeTags.VEGETABLES_POTATO)
              .addIngredient(Items.WHEAT)
              .addIngredient(Items.WHEAT_SEEDS)
              .unlockedByItems("has_tankard", BnCItems.TANKARD.get())
              .unlockedByItems("has_potato", Items.POTATO)
              .setRecipeBookTab(FermentingRecipeBookTab.DRINKS)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.MEAD.get(), 1000, SLOW_COOKING, MEDIUM_EXP)
              .addFluidIngredient(BnCFluids.HONEY_FLUID.get(), 1000)
              .addIngredient(Items.WHEAT)
              .addIngredient(Items.WHEAT_SEEDS)
              .addIngredient(Items.SWEET_BERRIES)
              .unlockedByItems("has_tankard", BnCItems.TANKARD.get())
              .setRecipeBookTab(FermentingRecipeBookTab.DRINKS)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.EGG_GROG.get(), 1000, SLOW_COOKING, MEDIUM_EXP)
              .addFluidIngredient(ForgeMod.MILK.get(), 1000)
              .addIngredient(ForgeTags.EGGS)
              .addIngredient(ForgeTags.CROPS_CABBAGE)
              .addIngredient(Items.SUGAR)
              .unlockedByItems("has_tankard", BnCItems.TANKARD.get())
              .setRecipeBookTab(FermentingRecipeBookTab.DRINKS)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.STRONGROOT_ALE.get(), 1000, NORMAL_COOKING, MEDIUM_EXP)
              .addFluidIngredient(BnCFluids.BEER.get(), 1000)
              .addIngredient(ForgeTags.VEGETABLES_BEETROOT)
              .addIngredient(ForgeTags.VEGETABLES_POTATO)
              .addIngredient(Items.BROWN_MUSHROOM)
              .addIngredient(ForgeTags.VEGETABLES_CARROT)
              .unlockedByItems("has_beer", BnCItems.BEER.get())
              .setRecipeBookTab(FermentingRecipeBookTab.DRINKS)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.RICE_WINE.get(), 1000, SLOW_COOKING, MEDIUM_EXP)
              .addFluidIngredient(Fluids.WATER, 1000)
              .addIngredient(ForgeTags.CROPS_RICE)
              .addIngredient(Items.BROWN_MUSHROOM)
              .unlockedByItems("has_tankard", BnCItems.TANKARD.get())
              .setRecipeBookTab(FermentingRecipeBookTab.DRINKS)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.GLITTERING_GRENADINE.get(), 1000, SLOW_COOKING, MEDIUM_EXP, 2)
              .addFluidIngredient(Fluids.WATER, 1000)
              .addIngredient(Items.GLOW_BERRIES)
              .addIngredient(Items.GLOWSTONE_DUST)
              .addIngredient(Items.GLOW_INK_SAC)
              .unlockedByItems("has_tankard", BnCItems.TANKARD.get())
              .setRecipeBookTab(FermentingRecipeBookTab.DRINKS)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.STEEL_TOE_STOUT.get(), 1000, NORMAL_COOKING, MEDIUM_EXP, 1)
              .addFluidIngredient(BnCFluids.STRONGROOT_ALE.get(), 1000)
              .addIngredient(Items.IRON_INGOT)
              .addIngredient(Items.CRIMSON_FUNGUS)
              .addIngredient(Items.NETHER_WART)
              .addIngredient(Items.WHEAT)
              .unlockedByItems("has_strongroot_ale", BnCItems.STRONGROOT_ALE.get())
              .setRecipeBookTab(FermentingRecipeBookTab.DRINKS)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.DREAD_NOG.get(), 1000, NORMAL_COOKING, MEDIUM_EXP, 1)
              .addFluidIngredient(BnCFluids.EGG_GROG.get(), 1000)
              .addIngredient(ForgeTags.EGGS)
              .addIngredient(Items.TURTLE_EGG)
              .addIngredient(Items.FERMENTED_SPIDER_EYE)
              .unlockedByItems("has_egg_grog", BnCItems.EGG_GROG.get())
              .setRecipeBookTab(FermentingRecipeBookTab.DRINKS)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.SACCHARINE_RUM.get(), 1000, NORMAL_COOKING, MEDIUM_EXP, 4)
              .addFluidIngredient(BnCFluids.MEAD.get(), 1000)
              .addIngredient(Items.SWEET_BERRIES)
              .addIngredient(Items.SUGAR_CANE)
              .addIngredient(Items.MELON)
              .unlockedByItems("has_mead", BnCItems.MEAD.get())
              .setRecipeBookTab(FermentingRecipeBookTab.DRINKS)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.PALE_JANE.get(), 1000, NORMAL_COOKING, MEDIUM_EXP, 4)
              .addFluidIngredient(BnCFluids.RICE_WINE.get(), 1000)
              .addIngredient(Items.HONEY_BOTTLE)
              .addIngredient(ModItems.TREE_BARK.get())
              .addIngredient(Items.LILY_OF_THE_VALLEY)
              .addIngredient(Items.SUGAR)
              .unlockedByItems("has_rice_wine", BnCItems.RICE_WINE.get())
              .setRecipeBookTab(FermentingRecipeBookTab.DRINKS)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.SALTY_FOLLY.get(), 1000, NORMAL_COOKING, MEDIUM_EXP, 2)
              .addFluidIngredient(BnCFluids.VODKA.get(), 1000)
              .addIngredient(Items.SEA_PICKLE)
              .addIngredient(Items.DRIED_KELP)
              .addIngredient(Items.SEAGRASS)
              .unlockedByItems("has_vodka", BnCItems.VODKA.get())
              .setRecipeBookTab(FermentingRecipeBookTab.DRINKS)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.BLOODY_MARY.get(), 1000, NORMAL_COOKING, MEDIUM_EXP, 4)
              .addFluidIngredient(BnCFluids.VODKA.get(), 1000)
              .addIngredient(ForgeTags.CROPS_TOMATO)
              .addIngredient(ForgeTags.CROPS_CABBAGE)
              .addIngredient(Items.SWEET_BERRIES)
              .unlockedByItems("has_vodka", BnCItems.VODKA.get())
              .setRecipeBookTab(FermentingRecipeBookTab.DRINKS)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.RED_RUM.get(), 1000, NORMAL_COOKING, MEDIUM_EXP, 5)
              .addFluidIngredient(BnCFluids.BLOODY_MARY.get(), 1000)
              .addIngredient(Items.CRIMSON_FUNGUS)
              .addIngredient(Items.NETHER_WART)
              .addIngredient(Items.FERMENTED_SPIDER_EYE)
              .addIngredient(Items.SHROOMLIGHT)
              .unlockedByItems("has_bloody_mary", BnCItems.BLOODY_MARY.get())
              .setRecipeBookTab(FermentingRecipeBookTab.DRINKS)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.WITHERING_DROSS.get(), 1000, SLOW_COOKING, LARGE_EXP, 5)
              .addFluidIngredient(BnCFluids.SALTY_FOLLY.get(), 1000)
              .addIngredient(Items.WITHER_ROSE)
              .addIngredient(Items.INK_SAC)
              .addIngredient(Items.NETHER_WART)
              .addIngredient(Items.BONE)
              .unlockedByItems("has_salty_folly", BnCItems.SALTY_FOLLY.get())
              .setRecipeBookTab(FermentingRecipeBookTab.DRINKS)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.FLAXEN_CHEESE.get(), 1000, SLOW_COOKING, MEDIUM_EXP, 4)
              .addFluidIngredient(ForgeMod.MILK.get(), 1000)
              .addIngredient(Items.BROWN_MUSHROOM)
              .addIngredient(Items.PUMPKIN_SEEDS)
              .addIngredient(Items.SUGAR)
              .unlockedByItems("has_pumpkin_seeds", Items.PUMPKIN_SEEDS)
              .setRecipeBookTab(FermentingRecipeBookTab.MEALS)
              .build(consumer);
      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCFluids.SCARLET_CHEESE.get(), 1000, SLOW_COOKING, MEDIUM_EXP, 5)
              .addFluidIngredient(ForgeMod.MILK.get(), 1000)
              .addIngredient(Items.CRIMSON_FUNGUS)
              .addIngredient(Items.NETHER_WART)
              .addIngredient(Items.SUGAR)
              .unlockedByItems("has_nether_wart", Items.NETHER_WART)
              .setRecipeBookTab(FermentingRecipeBookTab.MEALS)
              .build(consumer);


      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCItems.JERKY.get(), 3, SLOW_COOKING, MEDIUM_EXP, 4)
              .addIngredient(BnCTags.RAW_MEATS)
              .addIngredient(BnCTags.RAW_MEATS)
              .addIngredient(BnCTags.RAW_MEATS)
              .unlockedBy("has_raw_meat", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(BnCTags.RAW_MEATS).build()))
              .setRecipeBookTab(FermentingRecipeBookTab.MEALS)
              .build(consumer);


      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCItems.KIMCHI.get(), 2, SLOW_COOKING, MEDIUM_EXP, 4)
              .addIngredient(ForgeTags.CROPS_CABBAGE)
              .addIngredient(ForgeTags.VEGETABLES)
              .addIngredient(Items.KELP)
              .unlockedByItems("has_kelp", Items.KELP)
              .setRecipeBookTab(FermentingRecipeBookTab.MEALS)
              .build(consumer);

      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCItems.KIPPERS.get(), 2, SLOW_COOKING, MEDIUM_EXP, 4)
              .addIngredient(ForgeTags.RAW_FISHES)
              .addIngredient(ForgeTags.RAW_FISHES)
              .addIngredient(Items.KELP)
              .unlockedBy("has_fish", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ForgeTags.RAW_FISHES).build()))
              .setRecipeBookTab(FermentingRecipeBookTab.MEALS)
              .build(consumer);

      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCItems.PICKLED_PICKLES.get(), 2, SLOW_COOKING, MEDIUM_EXP, 2)
              .addFluidIngredient(BnCFluids.HONEY_FLUID.get(), 250)
              .addIngredient(Items.SEA_PICKLE)
              .addIngredient(Items.SEA_PICKLE)
              .addIngredient(Items.GLOW_BERRIES)
              .unlockedByItems("has_sea_pickle", Items.SEA_PICKLE)
              .setRecipeBookTab(FermentingRecipeBookTab.MEALS)
              .build(consumer);

      KegFermentingRecipeBuilder.kegFermentingRecipe(BnCItems.COCOA_FUDGE.get(), 1, SLOW_COOKING, MEDIUM_EXP, 2)
              .addFluidIngredient(ForgeMod.MILK.get(), 500)
              .addIngredient(Items.SUGAR)
              .addIngredient(Items.COCOA_BEANS)
              .addIngredient(Items.COCOA_BEANS)
              .unlockedByItems("has_cocoa_beans", Items.COCOA_BEANS)
              .setRecipeBookTab(FermentingRecipeBookTab.MEALS)
              .build(consumer);
   }


}
