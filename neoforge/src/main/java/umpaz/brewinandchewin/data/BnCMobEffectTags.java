package umpaz.brewinandchewin.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import umpaz.brewinandchewin.neoforge.BrewinAndChewinNeoForge;
import umpaz.brewinandchewin.common.registry.BnCEffects;
import umpaz.brewinandchewin.common.tag.BnCTags;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class BnCMobEffectTags extends IntrinsicHolderTagsProvider<MobEffect>
{
    public BnCMobEffectTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.MOB_EFFECT, lookupProvider, mobEffect -> ForgeRegistries.MOB_EFFECTS.getResourceKey(mobEffect).orElseThrow(), BrewinAndChewinNeoForge.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.registerModTags();
    }

    protected void registerModTags() {
        tag(BnCTags.MILK_BOTTLE_LOW_PRIORITY)
                .add(BnCEffects.TIPSY.get());
    }
}
