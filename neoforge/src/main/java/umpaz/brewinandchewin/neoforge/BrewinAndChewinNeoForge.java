package umpaz.brewinandchewin.neoforge;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.registry.*;
import umpaz.brewinandchewin.common.registry.BnCCreativeTabs;
import umpaz.brewinandchewin.neoforge.registry.BnCAttachments;
import umpaz.brewinandchewin.neoforge.registry.BnCFluidTypes;
import umpaz.brewinandchewin.neoforge.registry.BnCLootModifiers;
import umpaz.brewinandchewin.neoforge.platform.BnCPlatformHelperNeoForge;

@Mod(BrewinAndChewin.MODID)
public class BrewinAndChewinNeoForge {

    public BrewinAndChewinNeoForge(IEventBus eventBus) {
        BrewinAndChewin.setHelper(new BnCPlatformHelperNeoForge());
        NeoForgeMod.enableMilkFluid();
    }

    @EventBusSubscriber(modid = BrewinAndChewin.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerContent(RegisterEvent event) {
            registerMethod(event, NeoForgeRegistries.Keys.ATTACHMENT_TYPES, BnCAttachments::registerAll);
            registerMethod(event, Registries.BLOCK, BnCBlocks::registerAll);
            registerMethod(event, Registries.BLOCK_ENTITY_TYPE, BnCBlockEntityTypes::registerAll);
            registerMethod(event, Registries.CREATIVE_MODE_TAB, BnCCreativeTabs::registerAll);
            registerMethod(event, Registries.FLUID, BnCFluids::registerAll);
            registerMethod(event, NeoForgeRegistries.Keys.FLUID_TYPES, BnCFluidTypes::registerAll);
            registerMethod(event, Registries.ITEM, BnCItems::registerAll);
            registerMethod(event, Registries.LOOT_CONDITION_TYPE, BnCLootConditions::registerAll);
            registerMethod(event, Registries.LOOT_FUNCTION_TYPE, BnCLootFunctions::registerAll);
            registerMethod(event, NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, BnCLootModifiers::registerAll);
            registerMethod(event, Registries.MENU, BnCMenuTypes::registerAll);
            registerMethod(event, Registries.MOB_EFFECT, BnCEffects::registerAll);
            registerMethod(event, Registries.PARTICLE_TYPE, BnCParticleTypes::registerAll);
            registerMethod(event, Registries.RECIPE_TYPE, BnCRecipeTypes::registerAll);
            registerMethod(event, Registries.RECIPE_SERIALIZER, BnCRecipeSerializers::registerAll);
        }

        public static <T> void registerMethod(RegisterEvent event, ResourceKey<Registry<T>> registry, Runnable registerMethod) {
            if (event.getRegistryKey() == registry)
                registerMethod.run();
        }
    }

}
