package umpaz.brewinandchewin.data.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.tag.BnCTags;

import java.util.concurrent.CompletableFuture;

public class BnCEntityTypeTags extends EntityTypeTagsProvider
{
    public BnCEntityTypeTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, BrewinAndChewin.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.registerModTags();
    }

    protected void registerModTags() {
        tag(BnCTags.IMMUNE_TO_INTOXICATION)
                .addTag(EntityTypeTags.UNDEAD);
    }
}