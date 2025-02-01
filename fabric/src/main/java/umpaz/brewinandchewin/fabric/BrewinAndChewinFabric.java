package umpaz.brewinandchewin.fabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.level.block.ComposterBlock;
import umpaz.brewinandchewin.common.registry.BnCItems;

public class BrewinAndChewinFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        BnCItems.registerAll();
    }

    public static void registerCompostables() {
        ComposterBlock.COMPOSTABLES.put(BnCItems.KIMCHI, 0.5F);
        ComposterBlock.COMPOSTABLES.put(BnCItems.PICKLED_PICKLES, 0.5F);
        ComposterBlock.COMPOSTABLES.put(BnCItems.QUICHE_SLICE, 0.85F);
        ComposterBlock.COMPOSTABLES.put(BnCItems.QUICHE, 1.0F);
    }
}
