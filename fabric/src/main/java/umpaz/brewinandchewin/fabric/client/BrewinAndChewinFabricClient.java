package umpaz.brewinandchewin.fabric.client;

import io.github.fabricators_of_create.porting_lib.event.client.ColorHandlersCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.BnCClientSetup;
import umpaz.brewinandchewin.client.BrewinAndChewinClient;
import umpaz.brewinandchewin.client.gui.KegScreen;
import umpaz.brewinandchewin.client.gui.KegTooltip;
import umpaz.brewinandchewin.common.registry.BnCMenuTypes;
import umpaz.brewinandchewin.fabric.client.platform.BnCClientPlatformHelperFabric;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BrewinAndChewinFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BrewinAndChewinClient.init(new BnCClientPlatformHelperFabric());
        BrewinAndChewin.isClient = true;

        MenuScreens.register(BnCMenuTypes.KEG, KegScreen::new);
        BnCClientSetup.registerBlockEntityRenderers(BlockEntityRenderers::register);
        BnCClientSetup.registerParticles((particleType, spriteParticleRegistration) -> ParticleFactoryRegistry.getInstance().register(particleType, provider -> spriteParticleRegistration.create(provider)));
        TooltipComponentCallback.EVENT.register(data -> {
            if (KegTooltip.KegTooltipComponent.class.isAssignableFrom(data.getClass())) {
                return new KegTooltip((KegTooltip.KegTooltipComponent) data);
            }
            return null;
        });
        BnCClientSetup.registerReloadListeners(preparableReloadListener -> {
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
                @Override
                public ResourceLocation getFabricId() {
                    return preparableReloadListener.getId();
                }

                @Override
                public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
                    return preparableReloadListener.reload(preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2);
                }
            });
        });
        BnCClientSetup.registerColorHandlers((blockColor, block) -> {
            ColorHandlersCallback.BLOCK.register(blockColors ->
                    blockColors.register(blockColor, block));
        });
        PreparableModelLoadingPlugin.register(BnCClientSetup::getModels, (data, context) -> {
            context.addModels(data.stream().map(resourceLocation -> resourceLocation.withPath(path -> "brewinandchewin/coaster/" + path)).toList());
            context.resolveModel().register(context1 -> {
                if (context1.id().getPath().startsWith("brewinandchewin/coaster/") && BnCClientSetup.MODELS.contains(context1.id().withPath(string -> string.substring(24)))) {
                    return context1.getOrLoadModel(context1.id().withPath(string -> string.substring(24)));
                }
                return null;
            });
        });
    }
}
