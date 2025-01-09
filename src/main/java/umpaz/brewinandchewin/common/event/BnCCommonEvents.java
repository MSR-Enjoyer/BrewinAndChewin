package umpaz.brewinandchewin.common.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.capability.TipsyNumbedHeartsCapability;
import umpaz.brewinandchewin.common.item.BoozeItem;
import umpaz.brewinandchewin.common.network.BnCNetworkHandler;
import umpaz.brewinandchewin.common.network.clientbound.ClearKegFluidContainerComponentsPacket;
import umpaz.brewinandchewin.common.registry.BnCDamageTypes;
import umpaz.brewinandchewin.common.registry.BnCEffects;
import vectorwing.farmersdelight.common.registry.ModItems;

@Mod.EventBusSubscriber(modid = BrewinAndChewin.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BnCCommonEvents {
    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity living)
            event.addCapability(TipsyNumbedHeartsCapability.ID, new TipsyNumbedHeartsCapability(living));
    }

    @SubscribeEvent
    public static void onPlayerJoinLevel(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer)
            serverPlayer.getCapability(TipsyNumbedHeartsCapability.INSTANCE).ifPresent(cap -> cap.syncToPlayer(serverPlayer));
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity living && event.getEntity() instanceof ServerPlayer serverPlayer)
            living.getCapability(TipsyNumbedHeartsCapability.INSTANCE).ifPresent(cap -> cap.syncToPlayer(serverPlayer));
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            event.getOriginal().reviveCaps();

            event.getEntity().getCapability(TipsyNumbedHeartsCapability.INSTANCE).ifPresent(cap -> {
                cap.setFrom(event.getOriginal().getCapability(TipsyNumbedHeartsCapability.INSTANCE).orElse(null));
                cap.sync();
            });

            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        living.getCapability(TipsyNumbedHeartsCapability.INSTANCE).ifPresent(cap -> {
            if (cap.getNumbedHealth() > 0.0) {
                if (cap.getTicksUntilDamage() > 0)
                    cap.setTicksUntilDamage(cap.getTicksUntilDamage() - 1);

                if (cap.getTicksUntilDamage() <= 0 || !living.hasEffect(BnCEffects.TIPSY.get())) {
                    float health = living.getHealth() + living.getAbsorptionAmount();
                    int remainingHealth = Mth.ceil(Math.min(cap.getNumbedHealth() - (health % 1 > cap.getNumbedHealth() % 1 ? 1 : 0), health));
                    if (remainingHealth > 0)
                        living.hurt(living.damageSources().source(BnCDamageTypes.CARDIAC_ARREST), cap.getNumbedHealth());
                    cap.setNumbedHealth(0.0F);
                }
            }
            if (!living.level().isClientSide)
                cap.sync();
        });
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingHurtEvent event) { // Use LivingHurtEvent so we can run before Protection enchantments, Resistance and Absorption.
        LivingEntity target = event.getEntity();
        if (!target.hasEffect(BnCEffects.TIPSY.get()) || event.getSource().is(BnCDamageTypes.CARDIAC_ARREST))
            return;
        int amplifier = target.getEffect(BnCEffects.TIPSY.get()).getAmplifier();
        float maximumNumbedHealth = Mth.floor(8 + (amplifier * 0.9F) / 2);
        target.getCapability(TipsyNumbedHeartsCapability.INSTANCE).ifPresent(cap -> {
            float reducedAmount = event.getAmount() * (0.3F + 0.022F * amplifier);
            float numbedHealth = Math.min(cap.getNumbedHealth() + reducedAmount, maximumNumbedHealth);
            event.setAmount(Math.min(event.getAmount() - (numbedHealth - cap.getNumbedHealth()), target.getHealth()));
            cap.setNumbedHealth(numbedHealth);
            cap.setTicksUntilDamage(200 + 20 * amplifier);
            if (target instanceof Player)
                cap.sync();
        });
    }

    @SubscribeEvent
    public static void reduceTipsy(LivingEntityUseItemEvent.Finish event) {
        LivingEntity player = event.getEntity();
        if (player.hasEffect(BnCEffects.TIPSY.get())) {
            MobEffectInstance tipsy = player.getEffect(BnCEffects.TIPSY.get());
            if (event.getItem().isEdible() && !(event.getItem().getItem() instanceof BoozeItem)) {
                player.forceAddEffect(new MobEffectInstance(BnCEffects.TIPSY.get(), (int) (tipsy.getDuration() * .9f), tipsy.getAmplifier(), false, false, true), player);
            }
            else if (event.getItem().is(Items.MILK_BUCKET)) {
                player.forceAddEffect(new MobEffectInstance(BnCEffects.TIPSY.get(), (int) (tipsy.getDuration() * .5f), tipsy.getAmplifier(), false, false, true), player);
            }
        }
    }

    @SubscribeEvent
    public static void sendClearFluidContainerTextComponents(OnDatapackSyncEvent event) {
        BnCNetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new ClearKegFluidContainerComponentsPacket());
    }
}

