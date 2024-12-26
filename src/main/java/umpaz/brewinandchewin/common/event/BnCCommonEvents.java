package umpaz.brewinandchewin.common.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.capability.TipsyNumbedHeartsCapability;
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
            if (cap.getNumbedHealth() > 0.0F) {
                cap.setTicksUntilDamage(cap.getTicksUntilDamage() + 1);
                if (cap.getTicksUntilDamage() > 200) {
                    living.hurt(living.damageSources().source(BnCDamageTypes.CARDIAC_ARREST), cap.getNumbedHealth());
                    cap.setNumbedHealth(0.0F);
                }
                if (!living.level().isClientSide)
                    cap.sync();
            }
        });
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!target.hasEffect(BnCEffects.TIPSY.get()))
            return;
        int amplifier = target.getEffect(BnCEffects.TIPSY.get()).getAmplifier();
        float maximumNumbedHearts = Mth.floor((8 + (0.9F * amplifier)) / 2.0F) * 2.0F;
        target.getCapability(TipsyNumbedHeartsCapability.INSTANCE).ifPresent(cap -> {
            float reducedAmount = Math.min(event.getAmount() * (0.3F + 0.022F * amplifier), maximumNumbedHearts - cap.getNumbedHealth());
            cap.setNumbedHealth(cap.getNumbedHealth() + reducedAmount);
            cap.setTicksUntilDamage(0);
            if (target instanceof Player)
                cap.sync();
            event.setAmount(event.getAmount() - reducedAmount);
        });
    }

    @SubscribeEvent
    public static void reduceTipsy(final LivingEntityUseItemEvent.Finish event) {
        LivingEntity player = event.getEntity();
        if (player.hasEffect(BnCEffects.TIPSY.get())) {
            if (event.getItem().isEdible()) {
                MobEffectInstance tipsy = player.getEffect(BnCEffects.TIPSY.get());
                if (event.getItem().is(Items.MILK_BUCKET) || event.getItem().is(ModItems.MILK_BOTTLE.get())) {
                    player.forceAddEffect(new MobEffectInstance(BnCEffects.TIPSY.get(), (int) (tipsy.getDuration() * .5f), tipsy.getAmplifier(), false, false, true), player);
                }
                else {
                    player.forceAddEffect(new MobEffectInstance(BnCEffects.TIPSY.get(), (int) (tipsy.getDuration() * .9f), tipsy.getAmplifier(), false, false, true), player);
                }
            }
        }
    }
}

