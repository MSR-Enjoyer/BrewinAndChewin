package umpaz.brewinandchewin.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.ComposterBlock;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.attachment.RagingAttachment;
import umpaz.brewinandchewin.common.attachment.TipsyHeartsAttachment;
import umpaz.brewinandchewin.common.network.clientbound.ClearKegFluidContainerComponentsClientboundPacket;
import umpaz.brewinandchewin.common.network.clientbound.SyncNumbedHeartsClientboundPacket;
import umpaz.brewinandchewin.common.network.clientbound.SyncRagingStacksClientboundPacket;
import umpaz.brewinandchewin.common.network.serverbound.TransferKegRecipeServerboundPacket;
import umpaz.brewinandchewin.common.registry.BnCBlockEntityTypes;
import umpaz.brewinandchewin.common.registry.BnCBlocks;
import umpaz.brewinandchewin.common.registry.BnCCreativeTabs;
import umpaz.brewinandchewin.common.registry.BnCEffects;
import umpaz.brewinandchewin.common.registry.BnCFluids;
import umpaz.brewinandchewin.common.registry.BnCItems;
import umpaz.brewinandchewin.common.registry.BnCLootConditions;
import umpaz.brewinandchewin.common.registry.BnCLootFunctions;
import umpaz.brewinandchewin.common.registry.BnCMenuTypes;
import umpaz.brewinandchewin.common.registry.BnCParticleTypes;
import umpaz.brewinandchewin.common.registry.BnCRecipeSerializers;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.fabric.fluid.BnCFluidVariantAttributeHandler;
import umpaz.brewinandchewin.fabric.registry.BnCAttachments;
import umpaz.brewinandchewin.fabric.registry.BnCLootModifiers;

import java.util.Optional;

public class BrewinAndChewinFabric implements ModInitializer {
    private static MinecraftServer server;

    @Override
    public void onInitialize() {
        registerContents();
        registerNetwork();
        registerCompostables();
        registerFluidAttributeHandlers();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            BrewinAndChewinFabric.server = server;
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            BrewinAndChewinFabric.server = null;
        });

        EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
            if (entity instanceof LivingEntity living) {
                if (entity.hasAttached(BnCAttachments.TIPSY_HEARTS)) {
                    TipsyHeartsAttachment attachment = entity.getAttached(BnCAttachments.TIPSY_HEARTS);
                    BrewinAndChewin.getHelper().sendClientbound(player, new SyncNumbedHeartsClientboundPacket(living.getId(), attachment.getNumbedHealth(), attachment.getTicksUntilDamage()));
                }
                if (entity.hasAttached(BnCAttachments.RAGING)) {
                    RagingAttachment attachment = entity.getAttached(BnCAttachments.RAGING);
                    BrewinAndChewin.getHelper().sendClientbound(player, new SyncRagingStacksClientboundPacket(living.getId(), Optional.of(attachment.getStacks())));
                }
            }
        });
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof ServerPlayer) {
                if (entity.hasAttached(BnCAttachments.TIPSY_HEARTS)) {
                    TipsyHeartsAttachment attachment = entity.getAttached(BnCAttachments.TIPSY_HEARTS);
                    BrewinAndChewin.getHelper().sendClientboundTracking(entity, new SyncNumbedHeartsClientboundPacket(entity.getId(), attachment.getNumbedHealth(), attachment.getTicksUntilDamage()));
                }
                if (entity.hasAttached(BnCAttachments.RAGING)) {
                    RagingAttachment attachment = entity.getAttached(BnCAttachments.RAGING);
                    BrewinAndChewin.getHelper().sendClientboundTracking(entity, new SyncRagingStacksClientboundPacket(entity.getId(), Optional.of(attachment.getStacks())));
                }
            }
        });
    }

    public static MinecraftServer getServer() {
        return server;
    }

    private static void registerContents() {
        BnCAttachments.registerAll();
        BnCBlocks.registerAll();
        BnCBlockEntityTypes.registerAll();
        BnCCreativeTabs.registerAll();
        BnCEffects.registerAll();
        BnCFluids.registerAll();
        BnCItems.registerAll();
        BnCLootConditions.registerAll();
        BnCLootFunctions.registerAll();
        BnCLootModifiers.registerAll();
        BnCMenuTypes.registerAll();
        BnCParticleTypes.registerAll();
        BnCRecipeTypes.registerAll();
        BnCRecipeSerializers.registerAll();
    }

    private static void registerNetwork() {
        PayloadTypeRegistry.playS2C().register(ClearKegFluidContainerComponentsClientboundPacket.TYPE, ClearKegFluidContainerComponentsClientboundPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncNumbedHeartsClientboundPacket.TYPE, SyncNumbedHeartsClientboundPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncRagingStacksClientboundPacket.TYPE, SyncRagingStacksClientboundPacket.STREAM_CODEC);

        PayloadTypeRegistry.playC2S().register(TransferKegRecipeServerboundPacket.TYPE, TransferKegRecipeServerboundPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TransferKegRecipeServerboundPacket.TYPE, (payload, context) -> payload.handle(context.player()));
    }

    private static void registerCompostables() {
        ComposterBlock.COMPOSTABLES.put(BnCItems.KIMCHI, 0.5F);
        ComposterBlock.COMPOSTABLES.put(BnCItems.PICKLED_PICKLES, 0.5F);
        ComposterBlock.COMPOSTABLES.put(BnCItems.QUICHE_SLICE, 0.85F);
        ComposterBlock.COMPOSTABLES.put(BnCItems.QUICHE, 1.0F);
    }

    private static void registerFluidAttributeHandlers() {
        FluidVariantAttributes.register(BnCFluids.HONEY, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_HONEY, BnCFluidVariantAttributeHandler.INSTANCE);

        FluidVariantAttributes.register(BnCFluids.BEER, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_BEER, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.VODKA, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_VODKA, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.MEAD, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_MEAD, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.EGG_GROG, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_EGG_GROG, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.STRONGROOT_ALE, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_STRONGROOT_ALE, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.RICE_WINE, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_RICE_WINE, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.GLITTERING_GRENADINE, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_GLITTERING_GRENADINE, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.STEEL_TOE_STOUT, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_STEEL_TOE_STOUT, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.DREAD_NOG, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_DREAD_NOG, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.SACCHARINE_RUM, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_SACCHARINE_RUM, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.PALE_JANE, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_PALE_JANE, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.SALTY_FOLLY, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_SALTY_FOLLY, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.BLOODY_MARY, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_BLOODY_MARY, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.RED_RUM, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_RED_RUM, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.WITHERING_DROSS, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_WITHERING_DROSS, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.KOMBUCHA, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_KOMBUCHA, BnCFluidVariantAttributeHandler.INSTANCE);

        FluidVariantAttributes.register(BnCFluids.FLAXEN_CHEESE, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_FLAXEN_CHEESE, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.SCARLET_CHEESE, BnCFluidVariantAttributeHandler.INSTANCE);
        FluidVariantAttributes.register(BnCFluids.FLOWING_SCARLET_CHEESE, BnCFluidVariantAttributeHandler.INSTANCE);
    }
}
