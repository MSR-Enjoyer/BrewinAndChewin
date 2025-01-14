package umpaz.brewinandchewin.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;
import umpaz.brewinandchewin.common.loot.condition.AreaLocationCheck;
import umpaz.brewinandchewin.common.registry.BnCBlocks;
import umpaz.brewinandchewin.common.registry.BnCItems;
import umpaz.brewinandchewin.common.tag.BnCTags;
import vectorwing.farmersdelight.common.block.CabinetBlock;
import vectorwing.farmersdelight.common.block.FeastBlock;
import vectorwing.farmersdelight.common.block.PieBlock;
import vectorwing.farmersdelight.common.tag.ModTags;
import vectorwing.farmersdelight.common.utility.TextUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BnCAdvancements implements ForgeAdvancementProvider.AdvancementGenerator {
    // Make sure to exclude compatibility items such as Kombucha.
    private static final Item[] DRINKS = new Item[]{
            BnCItems.BEER.get(),
            BnCItems.VODKA.get(),
            BnCItems.MEAD.get(),
            BnCItems.EGG_GROG.get(),
            BnCItems.STRONGROOT_ALE.get(),
            BnCItems.SACCHARINE_RUM.get(),
            BnCItems.PALE_JANE.get(),
            BnCItems.SALTY_FOLLY.get(),
            BnCItems.STEEL_TOE_STOUT.get(),
            BnCItems.GLITTERING_GRENADINE.get(),
            BnCItems.BLOODY_MARY.get(),
            BnCItems.RED_RUM.get(),
            BnCItems.WITHERING_DROSS.get(),
            BnCItems.DREAD_NOG.get()
    };
    private static final Item[] MEALS = new Item[]{
            BnCItems.KIMCHI.get(),
            BnCItems.JERKY.get(),
            BnCItems.PICKLED_PICKLES.get(),
            BnCItems.KIPPERS.get(),
            BnCItems.COCOA_FUDGE.get(),
            BnCItems.VEGETABLE_OMELET.get(),
            BnCItems.CHEESY_PASTA.get(),
            BnCItems.SCARLET_PIEROGIES.get(),
            BnCItems.HORROR_LASAGNA.get(),
            BnCItems.PIZZA_SLICE.get(),
            BnCItems.FIERY_FONDUE.get(),
            BnCItems.HAM_AND_CHEESE_SANDWICH.get(),
            BnCItems.SWEET_BERRY_JAM.get(),
            BnCItems.GLOW_BERRY_MARMALADE.get(),
            BnCItems.APPLE_JELLY.get()
    };

    public BnCAdvancements() {
    }

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
        Advancement brewinAndChewin = Advancement.Builder.advancement().
                display(BnCItems.BEER.get(), Component.translatable("brewinandchewin.advancement.root"), Component.translatable("brewinandchewin.advancement.root.desc"), new ResourceLocation("minecraft:textures/block/spruce_planks.png"), FrameType.TASK, false, false, false).addCriterion("beer", InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[0]))
                .save(saver, BrewinAndChewin.asResource("main/root").toString());
        Advancement placeKeg = getAdvancement(brewinAndChewin, BnCItems.KEG.get(), Component.translatable("brewinandchewin.advancement.place_keg"), Component.translatable("brewinandchewin.advancement.place_keg.desc"), FrameType.TASK, true, true, false)
                .addCriterion("placed_keg", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(BnCBlocks.KEG.get()))
                .save(saver, BrewinAndChewin.asResource("main/place_keg").toString());
        Advancement placeTemperatureBlockNearKeg = getAdvancement(placeKeg, BnCItems.ICE_CRATE.get(), Component.translatable("brewinandchewin.advancement.place_temperature_block_near_keg"), Component.translatable("brewinandchewin.advancement.place_temperature_block_near_keg.desc"), FrameType.TASK, true, true, false)
                .addCriterion("placed_heat_source_near_keg", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(ModTags.HEAT_SOURCES).build())), AreaLocationCheck.checkArea(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BnCBlocks.KEG.get()).build()), KegBlockEntity.RANGE)))
                .addCriterion("placed_freeze_source_near_keg", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BnCTags.FREEZE_SOURCES).build())), AreaLocationCheck.checkArea(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BnCBlocks.KEG.get()).build()), KegBlockEntity.RANGE)))
                .requirements(RequirementsStrategy.OR)
                .save(saver, BrewinAndChewin.asResource("main/place_temperature_block_near_keg").toString());
        Advancement brewDrink = getAdvancement(placeKeg, BnCItems.VODKA.get(), Component.translatable("brewinandchewin.advancement.brew_drink"), Component.translatable("brewinandchewin.advancement.brew_drink.desc"), FrameType.TASK, true, true, false)
                .addCriterion("has_drink", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(BnCTags.FERMENTED_DRINKS).build()))
                .save(saver, BrewinAndChewin.asResource("main/brew_drink").toString());
        Advancement craftingProblem = getCraftingProblemAdvancement(getAdvancement(brewDrink, BnCItems.STEEL_TOE_STOUT.get(), Component.translatable("brewinandchewin.advancement.crafting_problem"), Component.translatable("brewinandchewin.advancement.crafting_problem.desc"), FrameType.CHALLENGE, true, true, false))
                .rewards(AdvancementRewards.Builder.experience(100))
                .save(saver, BrewinAndChewin.asResource("main/crafting_problem").toString());
        Advancement fermentCheese = getAdvancement(placeKeg, BnCItems.UNRIPE_FLAXEN_CHEESE_WHEEL.get(), Component.translatable("brewinandchewin.advancement.ferment_cheese"), Component.translatable("brewinandchewin.advancement.ferment_cheese.desc"), FrameType.TASK, true, true, false)
                .addCriterion("has_unripe_flaxen_cheese_wheel", InventoryChangeTrigger.TriggerInstance.hasItems(BnCItems.UNRIPE_FLAXEN_CHEESE_WHEEL.get()))
                .addCriterion("has_unripe_scarlet_cheese_wheel", InventoryChangeTrigger.TriggerInstance.hasItems(BnCItems.UNRIPE_SCARLET_CHEESE_WHEEL.get()))
                .requirements(RequirementsStrategy.OR)
                .save(saver, BrewinAndChewin.asResource("main/ferment_cheese").toString());
        Advancement cookFieryFondue = getAdvancement(fermentCheese, BnCItems.FIERY_FONDUE_POT.get(), Component.translatable("brewinandchewin.advancement.cook_fiery_fondue"), Component.translatable("brewinandchewin.advancement.cook_fiery_fondue.desc"), FrameType.CHALLENGE, true, true, false)
                .addCriterion("has_fiery_fondue_pot", InventoryChangeTrigger.TriggerInstance.hasItems(BnCItems.FIERY_FONDUE_POT.get()))
                .requirements(RequirementsStrategy.OR)
                .rewards(AdvancementRewards.Builder.experience(50))
                .save(saver, BrewinAndChewin.asResource("main/cook_fiery_fondue").toString());
        Advancement chefOfTheAges = getChefOfTheAgesAdvancement(getAdvancement(cookFieryFondue, BnCItems.PIZZA.get(), Component.translatable("brewinandchewin.advancement.chef_of_the_ages"), Component.translatable("brewinandchewin.advancement.chef_of_the_ages.desc"),  FrameType.CHALLENGE,true, true, false))
                .rewards(AdvancementRewards.Builder.experience(100))
                .save(saver, BrewinAndChewin.asResource("main/chef_of_the_ages").toString());
    }

    protected static Advancement.Builder getAdvancement(Advancement parent, ItemLike item, Component name, Component description, FrameType frameType, boolean showToast, boolean announceToChat, boolean hidden) {
        return Advancement.Builder.advancement().parent(parent).display(item, name, description, null, frameType, showToast, announceToChat, hidden);
    }

    protected static Advancement.Builder getCraftingProblemAdvancement(Advancement.Builder builder) {
        for (Item drink : DRINKS) {
            builder.addCriterion(drink.builtInRegistryHolder().key().location().toString(), ConsumeItemTrigger.TriggerInstance.usedItem(drink));
        }
        return builder;
    }

    protected static Advancement.Builder getChefOfTheAgesAdvancement(Advancement.Builder builder) {
        for (Item meal : MEALS) {
            builder.addCriterion(meal.builtInRegistryHolder().key().location().toString(), ConsumeItemTrigger.TriggerInstance.usedItem(meal));
        }
        return builder;
    }
}
