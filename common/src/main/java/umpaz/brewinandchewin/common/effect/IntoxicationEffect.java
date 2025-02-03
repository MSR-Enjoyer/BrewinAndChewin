package umpaz.brewinandchewin.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import umpaz.brewinandchewin.common.tag.BnCTags;

public class IntoxicationEffect extends MobEffect
{
    public IntoxicationEffect() {
        super(MobEffectCategory.HARMFUL, 0);
    }

    public static boolean canBeIntoxicated(LivingEntity entity) {
        return entity.getType().is(BnCTags.IMMUNE_TO_INTOXICATION);
    }
}