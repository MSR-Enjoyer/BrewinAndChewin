package umpaz.brewinandchewin.neoforge.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.attachment.RagingAttachment;
import umpaz.brewinandchewin.common.attachment.TipsyHeartsAttachment;
import umpaz.brewinandchewin.common.network.clientbound.ClearKegFluidContainerComponentsClientboundPacket;
import umpaz.brewinandchewin.common.network.clientbound.SyncNumbedHeartsClientboundPacket;
import umpaz.brewinandchewin.common.network.clientbound.SyncRagingStacksClientboundPacket;
import umpaz.brewinandchewin.common.registry.BnCDamageTypes;
import umpaz.brewinandchewin.common.registry.BnCEffects;
import umpaz.brewinandchewin.common.tag.BnCTags;
import umpaz.brewinandchewin.neoforge.registry.BnCAttachments;

import java.util.Optional;

@EventBusSubscriber(modid = BrewinAndChewin.MODID)
public class BnCCommonEvents {
    @SubscribeEvent
    public static void onPlayerJoinLevel(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.hasData(BnCAttachments.TIPSY_HEARTS)) {
                TipsyHeartsAttachment attachment = serverPlayer.getData(BnCAttachments.TIPSY_HEARTS);
                BrewinAndChewin.getHelper().sendClientboundTracking(serverPlayer, new SyncNumbedHeartsClientboundPacket(serverPlayer.getId(), attachment.getNumbedHealth(), attachment.getTicksUntilDamage()));
            }
            if (serverPlayer.hasData(BnCAttachments.RAGING)) {
                RagingAttachment attachment = serverPlayer.getData(BnCAttachments.RAGING);
                BrewinAndChewin.getHelper().sendClientboundTracking(serverPlayer, new SyncRagingStacksClientboundPacket(serverPlayer.getId(), Optional.of(attachment.getStacks())));
            }
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity living && event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (event.getTarget().hasData(BnCAttachments.TIPSY_HEARTS)) {
                TipsyHeartsAttachment attachment = event.getTarget().getData(BnCAttachments.TIPSY_HEARTS);
                BrewinAndChewin.getHelper().sendClientbound(serverPlayer, new SyncNumbedHeartsClientboundPacket(living.getId(), attachment.getNumbedHealth(), attachment.getTicksUntilDamage()));
            }
            if (event.getTarget().hasData(BnCAttachments.RAGING)) {
                RagingAttachment attachment = event.getTarget().getData(BnCAttachments.RAGING);
                BrewinAndChewin.getHelper().sendClientbound(serverPlayer, new SyncRagingStacksClientboundPacket(living.getId(), Optional.of(attachment.getStacks())));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof LivingEntity living) {
            TipsyHeartsAttachment attachment = BrewinAndChewin.getHelper().getTipsyHeartsAttachment(living);
            if (attachment != null && attachment.getNumbedHealth() > 0.0) {
                if (attachment.getTicksUntilDamage() > 0)
                    attachment.setTicksUntilDamage(attachment.getTicksUntilDamage() - 1);

                if (attachment.getTicksUntilDamage() <= 0 || !living.hasEffect(BnCEffects.TIPSY) && !living.level().isClientSide) {
                    float health = living.getHealth() + living.getAbsorptionAmount();
                    int remainingHealth = Mth.ceil(Math.min(attachment.getNumbedHealth() - (health % 1 > attachment.getNumbedHealth() % 1 ? 1 : 0), health));
                    if (remainingHealth > 0)
                        living.hurt(living.damageSources().source(BnCDamageTypes.CARDIAC_ARREST), attachment.getNumbedHealth());
                    attachment.setNumbedHealth(0.0F);
                    BrewinAndChewin.getHelper().sendClientboundTracking(living, new SyncNumbedHeartsClientboundPacket(living.getId(), attachment.getNumbedHealth(), attachment.getTicksUntilDamage()));
                }
            }
            RagingAttachment.tick(living);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        Entity attacker = event.getSource().getEntity();
        LivingEntity target = event.getEntity();

        if (target.hasEffect(BnCEffects.TIPSY) && !event.getSource().is(BnCDamageTypes.CARDIAC_ARREST)) {
            int amplifier = target.getEffect(BnCEffects.TIPSY).getAmplifier();
            float maximumNumbedHealth = Mth.clamp(Mth.floor((2 + (amplifier * 1.6F)) / 2) * 2, 1, target.getMaxHealth() - 2);
            TipsyHeartsAttachment attachment = BrewinAndChewin.getHelper().getTipsyHeartsAttachment(target);
            if (attachment != null) {
                float numbedHealth = Math.min(attachment.getNumbedHealth() + event.getNewDamage(), maximumNumbedHealth);
                if (numbedHealth - attachment.getNumbedHealth() <= target.getHealth())
                    event.setNewDamage(event.getNewDamage() - (numbedHealth - attachment.getNumbedHealth()));
                int ticksUntilDamage = 200 + 20 * amplifier;
                attachment.setNumbedHealth(numbedHealth);
                attachment.setTicksUntilDamage(ticksUntilDamage);
                PacketDistributor.sendToPlayersTrackingEntity(target, new SyncNumbedHeartsClientboundPacket(target.getId(), numbedHealth, ticksUntilDamage));
            }
        }
        if (attacker instanceof LivingEntity living && (!(living instanceof Player player) || player.getAttackStrengthScale(0.0F) > 0.8F) && living.hasEffect(BnCEffects.RAGING) && event.getSource().is(BnCTags.TRIGGERS_RAGING)) {
            RagingAttachment attachment = BrewinAndChewin.getHelper().getRagingAttachment(living);
            if (attachment != null) {
                int stacks = Math.min(4, attachment.getStacks() + 1);
                if (stacks != attachment.getStacks() && !target.level().isClientSide()) {
                    double heightAddition = living.getY(1.0D) - living.getY(0.5D);
                    ((ServerLevel) target.level()).sendParticles(RagingAttachment.getParticleType(stacks, 0.5F), target.getX(), target.getY(0.5), target.getZ(), 12, target.getRandom().nextDouble() * 0.4 - 0.2, target.getRandom().nextDouble() * heightAddition * 2 - heightAddition, target.getRandom().nextDouble() * 0.4 - 0.2, 0.0);
                }
                attachment.setStacks(stacks);
                attachment.setTicksUntilReset(Mth.ceil(RagingAttachment.RESET_TICK_MULTIPLIER * (living instanceof Player player ? player.getCurrentItemAttackStrengthDelay() : 30)));
            }
        }
    }

    @SubscribeEvent
    public static void sendClearFluidContainerTextComponents(OnDatapackSyncEvent event) {
        PacketDistributor.sendToAllPlayers(new ClearKegFluidContainerComponentsClientboundPacket());
    }
}

