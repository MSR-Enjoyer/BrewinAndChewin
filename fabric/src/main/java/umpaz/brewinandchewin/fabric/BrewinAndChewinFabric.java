package umpaz.brewinandchewin.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.ComposterBlock;
import umpaz.brewinandchewin.common.registry.BnCBlockEntityTypes;
import umpaz.brewinandchewin.common.registry.BnCBlocks;
import umpaz.brewinandchewin.common.registry.BnCCreativeTabs;
import umpaz.brewinandchewin.common.registry.BnCEffects;
import umpaz.brewinandchewin.common.registry.BnCFluids;
import umpaz.brewinandchewin.common.registry.BnCItems;
import umpaz.brewinandchewin.common.registry.BnCLootConditions;
import umpaz.brewinandchewin.common.registry.BnCLootFunctions;
import umpaz.brewinandchewin.common.registry.BnCMenuTypes;
import umpaz.brewinandchewin.common.registry.BnCParticleTypes;
import umpaz.brewinandchewin.common.registry.BnCRecipeSerializers;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.fabric.registry.BnCAttachments;
import umpaz.brewinandchewin.fabric.registry.BnCLootModifiers;

public class BrewinAndChewinFabric implements ModInitializer {
    private static MinecraftServer server;

    @Override
    public void onInitialize() {
        registerContents();
        registerCompostables();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            BrewinAndChewinFabric.server = server;
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            BrewinAndChewinFabric.server = null;
        });
    }

    public static MinecraftServer getServer() {
        return server;
    }

    private static void registerContents() {
        BnCAttachments.registerAll();
        BnCBlocks.registerAll();
        BnCBlockEntityTypes.registerAll();
        BnCCreativeTabs.registerAll();
        BnCEffects.registerAll();
        BnCFluids.registerAll();
        BnCItems.registerAll();
        BnCLootConditions.registerAll();
        BnCLootFunctions.registerAll();
        BnCLootModifiers.registerAll();
        BnCMenuTypes.registerAll();
        BnCParticleTypes.registerAll();
        BnCRecipeTypes.registerAll();
        BnCRecipeSerializers.registerAll();
    }

    private static void registerCompostables() {
        ComposterBlock.COMPOSTABLES.put(BnCItems.KIMCHI, 0.5F);
        ComposterBlock.COMPOSTABLES.put(BnCItems.PICKLED_PICKLES, 0.5F);
        ComposterBlock.COMPOSTABLES.put(BnCItems.QUICHE_SLICE, 0.85F);
        ComposterBlock.COMPOSTABLES.put(BnCItems.QUICHE, 1.0F);
    }
}
