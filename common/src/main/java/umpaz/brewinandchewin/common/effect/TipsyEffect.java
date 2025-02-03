package umpaz.brewinandchewin.common.effect;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3f;
import umpaz.brewinandchewin.client.particle.DrunkBubbleParticleOptions;

public class TipsyEffect extends MobEffect {

    public TipsyEffect() {
        super(MobEffectCategory.NEUTRAL, 0);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide && entity.level().random.nextInt(Math.max(13 - amplifier, 4)) == 0) {
            entity.level().addParticle(getParticle(), entity.getRandomX(1.0D), entity.getEyeY() - entity.getRandom().nextDouble() + .25d, entity.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
        }
        return true;
    }

    public ParticleOptions getParticle() {
        return new DrunkBubbleParticleOptions(new Vector3f(((getBubbleColor() >> 16) & 0xFF) / 255f, ((getBubbleColor() >> 8) & 0xFF) / 255f, (getBubbleColor() & 0xFF) / 255f), 0.25f);
    }

    public int getBubbleColor() {
        return 13208334;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}