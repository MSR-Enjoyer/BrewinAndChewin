package umpaz.brewinandchewin.common.effect;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Vector3f;
import umpaz.brewinandchewin.client.particle.DrunkBubbleParticleOptions;

import java.util.ArrayList;
import java.util.List;

public class TipsyEffect extends MobEffect {

    public TipsyEffect() {
        super(MobEffectCategory.NEUTRAL, 0);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide && entity.level().random.nextInt(13 - amplifier) == 0) {
            entity.level().addParticle(getParticle(), entity.getRandomX(1.0D), entity.getEyeY() - entity.getRandom().nextDouble() + .25d, entity.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return super.getAttributeModifierValue(amplifier, modifier);
    }

    public ParticleOptions getParticle() {
        return new DrunkBubbleParticleOptions(new Vector3f(((getBubbleColor() >> 16) & 0xFF) / 255f, ((getBubbleColor() >> 8) & 0xFF) / 255f, (getBubbleColor() & 0xFF) / 255f), 0.25f);
    }

    public int getBubbleColor() {
        return 13208334;
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}