package umpaz.brewinandchewin.common.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.particle.DrunkBubbleParticleOptions;

public class BnCParticleTypes {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BrewinAndChewin.MODID);

    public static final RegistryObject<SimpleParticleType> FOG = PARTICLE_TYPES.register("fog",
            () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> RAGING_STAGE_1 = PARTICLE_TYPES.register("raging_stage_1",
            () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> RAGING_STAGE_2 = PARTICLE_TYPES.register("raging_stage_2",
            () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> RAGING_STAGE_3 = PARTICLE_TYPES.register("raging_stage_3",
            () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> RAGING_STAGE_4 = PARTICLE_TYPES.register("raging_stage_4",
            () -> new SimpleParticleType(false));


   public static final RegistryObject<ParticleType<DrunkBubbleParticleOptions>> DRUNK_BUBBLE = PARTICLE_TYPES.register("drunk_bubble", () -> new ParticleType<>(false, DrunkBubbleParticleOptions.DESERIALIZER) {
      @Override
      public Codec<DrunkBubbleParticleOptions> codec() {
         return DrunkBubbleParticleOptions.CODEC;
      }
   });
}
