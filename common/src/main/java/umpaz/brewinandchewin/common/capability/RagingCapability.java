package umpaz.brewinandchewin.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.particle.RagingParticleOptions;
import umpaz.brewinandchewin.common.network.BnCNetworkHandler;
import umpaz.brewinandchewin.common.network.clientbound.SyncRagingStacksClientboundPacket;
import umpaz.brewinandchewin.common.registry.BnCEffects;

import java.util.UUID;

public class RagingCapability implements ICapabilitySerializable<CompoundTag> {
    public static final float RESET_TICK_MULTIPLIER = 2.5F;
    public static final ResourceLocation ID = BrewinAndChewin.asResource("raging");
    public static final Capability<RagingCapability> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});
    private final LazyOptional<RagingCapability> thisOptional = LazyOptional.of(() -> this);

    public RagingCapability() {
    }

    private int stacks = 0;
    private int ticksUntilReset = 0;
    private int previousStacks = 0;

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

    private static final UUID RAGING_ATTRIBUTE_UUID = UUID.fromString("70661c2d-8c96-4757-971f-1ae56f1422fe");

    public static void tick(LivingEntity living) {
        living.getCapability(INSTANCE).ifPresent(cap -> {
            if (!living.level().isClientSide()) {
                if (cap.getTicksUntilReset() <= 0 &&  cap.getStacks() > 0) {
                    cap.setStacks(cap.getStacks() - 1);
                    cap.setTicksUntilReset(Mth.ceil(RESET_TICK_MULTIPLIER * (living instanceof Player player ? player.getCurrentItemAttackStrengthDelay() : 30)));
                }

                if (cap.getStacks() <= 0 || !living.hasEffect(BnCEffects.RAGING.get())) {
                    if (living.getAttributes().hasModifier(Attributes.ATTACK_SPEED, RAGING_ATTRIBUTE_UUID))
                        living.getAttribute(Attributes.ATTACK_SPEED).removeModifier(RAGING_ATTRIBUTE_UUID);
                    if (cap.previousStacks != 0) {
                        cap.previousStacks = 0;
                        cap.setStacks(0);
                        cap.setTicksUntilReset(0);
                        BnCNetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> living), new SyncRagingStacksClientboundPacket(living.getId(), 0));
                    }
                    return;
                }

                cap.setTicksUntilReset(cap.getTicksUntilReset() - 1);
                if (cap.previousStacks != cap.stacks) {
                    if (living.getAttributes().hasModifier(Attributes.ATTACK_SPEED, RAGING_ATTRIBUTE_UUID))
                        living.getAttribute(Attributes.ATTACK_SPEED).removeModifier(RAGING_ATTRIBUTE_UUID);
                    living.getAttribute(Attributes.ATTACK_SPEED).addTransientModifier(new AttributeModifier(RAGING_ATTRIBUTE_UUID, "Raging attack speed increase", Math.min(0.8, 0.05 * cap.getStacks() + 0.025 * living.getEffect(BnCEffects.RAGING.get()).getAmplifier() * cap.getStacks()), AttributeModifier.Operation.MULTIPLY_TOTAL));
                    BnCNetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> living), new SyncRagingStacksClientboundPacket(living.getId(), cap.getStacks()));
                }
                cap.previousStacks = cap.stacks;
            }

            if (living.hasEffect(BnCEffects.RAGING.get()) && living.getEffect(BnCEffects.RAGING.get()).isVisible() && living.getRandom().nextInt(cap.getStacks() > 0 ? 3 : 20) == 0) {
                double heightAddition = living.getBbHeight() - living.getEyeHeight();
                living.level().addParticle(getParticleType(cap.getStacks(), 0.75F), living.getRandomX(0.7D), living.getRandom().nextDouble() * heightAddition * 2 + living.getEyeY() - heightAddition, living.getRandomZ(0.7D), (0.5 - living.getRandom().nextDouble()) * 0.1F, 0.0, (0.5 - living.getRandom().nextDouble()) * 0.1F);
            }
        });
    }

    public static ParticleOptions getParticleType(int stacks, float size) {
        return switch (stacks) {
            case 0, 1 -> new RagingParticleOptions.StageOne(size);
            case 2 -> new RagingParticleOptions.StageTwo(size);
            case 3 -> new RagingParticleOptions.StageThree(size);
            default -> new RagingParticleOptions.StageFour(size);
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return INSTANCE.orEmpty(cap, thisOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("stacks", stacks);
        tag.putInt("ticks_until_reset", ticksUntilReset);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        stacks = nbt.getInt("stacks");
        ticksUntilReset = nbt.getInt("ticks_until_reset");
    }
}
