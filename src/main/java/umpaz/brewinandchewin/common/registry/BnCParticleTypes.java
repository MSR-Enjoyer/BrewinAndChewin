package umpaz.brewinandchewin.common.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.particle.DrunkBubbleParticleOptions;
import umpaz.brewinandchewin.client.particle.RagingParticleOptions;

public class BnCParticleTypes {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BrewinAndChewin.MODID);

    public static final RegistryObject<SimpleParticleType> FOG = PARTICLE_TYPES.register("fog",
            () -> new SimpleParticleType(true));
    public static final RegistryObject<ParticleType<RagingParticleOptions.StageOne>> RAGING_STAGE_1 = PARTICLE_TYPES.register("raging_stage_1",
            () -> new ParticleType<>(false, RagingParticleOptions.StageOne.DESERIALIZER) {
                @Override
                public Codec<RagingParticleOptions.StageOne> codec() {
                    return RagingParticleOptions.StageOne.CODEC;
                }
            });
    public static final RegistryObject<ParticleType<RagingParticleOptions.StageTwo>> RAGING_STAGE_2 = PARTICLE_TYPES.register("raging_stage_2",
            () -> new ParticleType<>(false, RagingParticleOptions.StageTwo.DESERIALIZER) {
                @Override
                public Codec<RagingParticleOptions.StageTwo> codec() {
                    return RagingParticleOptions.StageTwo.CODEC;
                }
            });
    public static final RegistryObject<ParticleType<RagingParticleOptions.StageThree>> RAGING_STAGE_3 = PARTICLE_TYPES.register("raging_stage_3",
            () -> new ParticleType<>(false, RagingParticleOptions.StageThree.DESERIALIZER) {
                @Override
                public Codec<RagingParticleOptions.StageThree> codec() {
                    return RagingParticleOptions.StageThree.CODEC;
                }
            });
    public static final RegistryObject<ParticleType<RagingParticleOptions.StageFour>> RAGING_STAGE_4 = PARTICLE_TYPES.register("raging_stage_4",
            () -> new ParticleType<>(false, RagingParticleOptions.StageFour.DESERIALIZER) {
                @Override
                public Codec<RagingParticleOptions.StageFour> codec() {
                    return RagingParticleOptions.StageFour.CODEC;
                }
            });


   public static final RegistryObject<ParticleType<DrunkBubbleParticleOptions>> DRUNK_BUBBLE = PARTICLE_TYPES.register("drunk_bubble", () -> new ParticleType<>(false, DrunkBubbleParticleOptions.DESERIALIZER) {
      @Override
      public Codec<DrunkBubbleParticleOptions> codec() {
         return DrunkBubbleParticleOptions.CODEC;
      }
   });
}
