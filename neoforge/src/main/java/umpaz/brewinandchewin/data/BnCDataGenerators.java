package umpaz.brewinandchewin.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.internal.NeoForgeAdvancementProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.data.recipe.BnCEntityTypeTags;
import umpaz.brewinandchewin.neoforge.BrewinAndChewinNeoForge;
import umpaz.brewinandchewin.data.loot.BnCBlockLoot;
import vectorwing.farmersdelight.data.BlockTags;
import vectorwing.farmersdelight.data.ItemTags;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = BrewinAndChewin.MODID, bus = EventBusSubscriber.Bus.MOD)
public class BnCDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        BnCBlockTags blockTags = new BnCBlockTags(output, lookupProvider, helper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new BnCItemTags(output, lookupProvider, blockTags.contentsGetter(), helper));
        generator.addProvider(event.includeServer(), new BnCMobEffectTags(output, lookupProvider, helper));
        generator.addProvider(event.includeServer(), new BnCEntityTypeTags(output, lookupProvider, helper));
        generator.addProvider(event.includeServer(), new BnCDamageTypeTags(output, lookupProvider, helper));
        generator.addProvider(event.includeServer(), new BnCRecipes(output, lookupProvider));
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Collections.emptySet(), List.of(
                new LootTableProvider.SubProviderEntry(BnCBlockLoot::new, LootContextParamSets.BLOCK)
        ), lookupProvider));
        generator.addProvider(event.includeServer(), new AdvancementProvider(output, lookupProvider, helper, List.of(new BnCAdvancements())));
    }
}
