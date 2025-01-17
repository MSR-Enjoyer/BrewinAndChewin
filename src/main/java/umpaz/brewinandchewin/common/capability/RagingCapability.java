package umpaz.brewinandchewin.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.network.BnCNetworkHandler;
import umpaz.brewinandchewin.common.network.clientbound.SyncRagingStacksClientboundPacket;
import umpaz.brewinandchewin.common.registry.BnCEffects;
import umpaz.brewinandchewin.common.registry.BnCParticleTypes;

import java.util.UUID;

public class RagingCapability implements ICapabilitySerializable<CompoundTag> {
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
                if (cap.getStacks() <= 0 || cap.getTicksUntilReset() <= 0 || !living.hasEffect(BnCEffects.RAGING.get())) {
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

            if (living.hasEffect(BnCEffects.RAGING.get()) && cap.getStacks() > 0 && living.getEffect(BnCEffects.RAGING.get()).isVisible() && living.getRandom().nextInt(3) == 0) {
                double heightAddition = living.getBbHeight() - living.getEyeHeight();
                living.level().addParticle(getParticleType(cap.getStacks()), living.getRandomX(0.7D), living.getRandom().nextDouble() * heightAddition + living.getEyeY() - heightAddition * 2, living.getRandomZ(0.7D), 0.0, 0.0, 0.0);
            }
        });
    }

    private static ParticleOptions getParticleType(int stacks) {
        return switch (stacks) {
            case 1 -> BnCParticleTypes.RAGING_STAGE_1.get();
            case 2 -> BnCParticleTypes.RAGING_STAGE_2.get();
            case 3 -> BnCParticleTypes.RAGING_STAGE_3.get();
            default -> BnCParticleTypes.RAGING_STAGE_4.get();
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
