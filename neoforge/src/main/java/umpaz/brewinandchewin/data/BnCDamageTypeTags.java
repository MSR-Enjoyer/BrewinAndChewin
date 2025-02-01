package umpaz.brewinandchewin.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.common.data.ExistingFileHelper;
import umpaz.brewinandchewin.neoforge.BrewinAndChewinNeoForge;
import umpaz.brewinandchewin.common.tag.BnCTags;

import java.util.concurrent.CompletableFuture;

public class BnCDamageTypeTags extends DamageTypeTagsProvider
{
    public BnCDamageTypeTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, BrewinAndChewinNeoForge.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.registerModTags();
    }

    protected void registerModTags() {
        tag(BnCTags.TRIGGERS_RAGING)
                .add(DamageTypes.MOB_ATTACK)
                .add(DamageTypes.PLAYER_ATTACK);
    }
}