package umpaz.brewinandchewin.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.registry.BnCDamageTypes;
import umpaz.brewinandchewin.common.tag.BnCTags;

import java.util.concurrent.CompletableFuture;

public class BnCDamageTypeTags extends DamageTypeTagsProvider
{
    public BnCDamageTypeTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, BrewinAndChewin.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.registerModTags();
    }

    protected void registerModTags() {
        tag(DamageTypeTags.BYPASSES_ARMOR)
                .add(BnCDamageTypes.CARDIAC_ARREST);
        tag(DamageTypeTags.BYPASSES_COOLDOWN)
                .add(BnCDamageTypes.CARDIAC_ARREST);
        tag(DamageTypeTags.BYPASSES_EFFECTS)
                .add(BnCDamageTypes.CARDIAC_ARREST);
        tag(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                .add(BnCDamageTypes.CARDIAC_ARREST);
        tag(DamageTypeTags.BYPASSES_RESISTANCE)
                .add(BnCDamageTypes.CARDIAC_ARREST);
        tag(DamageTypeTags.BYPASSES_SHIELD)
                .add(BnCDamageTypes.CARDIAC_ARREST);
        tag(DamageTypeTags.BYPASSES_WOLF_ARMOR)
                .add(BnCDamageTypes.CARDIAC_ARREST);

        tag(BnCTags.DamageTypes.TRIGGERS_RAGING)
                .add(DamageTypes.MOB_ATTACK)
                .add(DamageTypes.PLAYER_ATTACK);
    }
}