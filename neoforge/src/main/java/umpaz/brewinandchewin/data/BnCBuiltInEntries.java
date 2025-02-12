package umpaz.brewinandchewin.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import umpaz.brewinandchewin.BrewinAndChewin;
import vectorwing.farmersdelight.FarmersDelight;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class BnCBuiltInEntries extends DatapackBuiltinEntriesProvider
{
    public BnCBuiltInEntries(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, RegistrySetBuilder setBuilder) {
        super(pOutput, pLookupProvider, setBuilder, Set.of(BrewinAndChewin.MODID, FarmersDelight.MODID));
    }

}