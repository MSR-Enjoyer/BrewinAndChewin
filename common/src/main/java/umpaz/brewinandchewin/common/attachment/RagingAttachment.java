package umpaz.brewinandchewin.common.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.particle.RagingParticleOptions;
import umpaz.brewinandchewin.common.network.clientbound.SyncRagingStacksClientboundPacket;
import umpaz.brewinandchewin.common.registry.BnCEffects;

import java.util.Optional;

public class RagingAttachment {
    public static final float RESET_TICK_MULTIPLIER = 2.5F;
    public static final ResourceLocation ID = BrewinAndChewin.asResource("raging");
    public static final Codec<RagingAttachment> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("stacks").forGetter(RagingAttachment::getStacks),
            Codec.INT.fieldOf("ticks_until_reset").forGetter(RagingAttachment::getTicksUntilReset)
    ).apply(inst, RagingAttachment::new));

    private int stacks;
    private int ticksUntilReset;
    private int previousStacks = 0;

    public RagingAttachment(int stacks, int ticksUntilReset) {
        this.stacks = stacks;
        this.ticksUntilReset = ticksUntilReset;
    }


    public int getStacks() {
        return stacks;
    }

    public void setStacks(int value) {
        stacks = value;
    }

    public int getTicksUntilReset() {
        return ticksUntilReset;
    }

    public void setTicksUntilReset(int value) {
        ticksUntilReset = value;
    }

    private static final ResourceLocation RAGING_ATTRIBUTE_ID = BrewinAndChewin.asResource("raging");

    public static void tick(LivingEntity living) {
        RagingAttachment attachment = BrewinAndChewin.getHelper().getRagingAttachment(living);
        if (attachment == null) {
            if (!living.hasEffect(BnCEffects.RAGING))
                return;
            BrewinAndChewin.getHelper().setRagingAttachment(living, new RagingAttachment(0, 0));
        }

        if (!living.level().isClientSide()) {
            if (attachment.getTicksUntilReset() <= 0 && attachment.getStacks() > 0) {
                attachment.setStacks(attachment.getStacks() - 1);
                attachment.setTicksUntilReset(Mth.ceil(RESET_TICK_MULTIPLIER * (living instanceof Player player ? player.getCurrentItemAttackStrengthDelay() : 30)));
            }

            if (attachment.getStacks() <= 0 || !living.hasEffect(BnCEffects.RAGING)) {
                if (living.getAttributes().hasModifier(Attributes.ATTACK_SPEED, RAGING_ATTRIBUTE_ID))
                    living.getAttribute(Attributes.ATTACK_SPEED).removeModifier(RAGING_ATTRIBUTE_ID);
                if (attachment.previousStacks != 0) {
                    attachment.previousStacks = 0;
                    BrewinAndChewin.getHelper().setRagingAttachment(living, null);
                    BrewinAndChewin.getHelper().sendClientboundTracking(living, new SyncRagingStacksClientboundPacket(living.getId(), Optional.empty()));
                }
                return;
            }

            attachment.setTicksUntilReset(attachment.getTicksUntilReset() - 1);
            if (attachment.previousStacks != attachment.stacks) {
                if (living.getAttributes().hasModifier(Attributes.ATTACK_SPEED, RAGING_ATTRIBUTE_ID))
                    living.getAttribute(Attributes.ATTACK_SPEED).removeModifier(RAGING_ATTRIBUTE_ID);
                living.getAttribute(Attributes.ATTACK_SPEED).addTransientModifier(new AttributeModifier(RAGING_ATTRIBUTE_ID, Math.min(0.8, 0.05 * attachment.getStacks() + 0.025 * living.getEffect(BnCEffects.RAGING).getAmplifier() * attachment.getStacks()), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                BrewinAndChewin.getHelper().sendClientboundTracking(living, new SyncRagingStacksClientboundPacket(living.getId(), Optional.of(attachment.getStacks())));
            }
            attachment.previousStacks = attachment.stacks;
        }

        if (living.hasEffect(BnCEffects.RAGING) && living.getEffect(BnCEffects.RAGING).isVisible() && living.getRandom().nextInt(attachment.getStacks() > 0 ? 3 : 20) == 0) {
            double heightAddition = living.getBbHeight() - living.getEyeHeight();
            living.level().addParticle(getParticleType(attachment.getStacks(), 0.75F), living.getRandomX(0.7D), living.getRandom().nextDouble() * heightAddition * 2 + living.getEyeY() - heightAddition, living.getRandomZ(0.7D), (0.5 - living.getRandom().nextDouble()) * 0.1F, 0.0, (0.5 - living.getRandom().nextDouble()) * 0.1F);
        }
    }

    public static ParticleOptions getParticleType(int stacks, float size) {
        return switch (stacks) {
            case 0, 1 -> new RagingParticleOptions.StageOne(size);
            case 2 -> new RagingParticleOptions.StageTwo(size);
            case 3 -> new RagingParticleOptions.StageThree(size);
            default -> new RagingParticleOptions.StageFour(size);
        };
    }
}
