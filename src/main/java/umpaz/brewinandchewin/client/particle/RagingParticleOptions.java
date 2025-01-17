package umpaz.brewinandchewin.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import umpaz.brewinandchewin.common.registry.BnCParticleTypes;

import java.util.function.Function;

public abstract class RagingParticleOptions implements ParticleOptions {
    private final float size;

    public RagingParticleOptions(float size) {
        this.size = size;
    }

    public float size() {
        return size;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(size);
    }

    @Override
    public String writeToString() {
        return "";
    }

    public static <T extends RagingParticleOptions> Codec<T> createCodec(Function<T, Float> sizeFunction, Function<Float, T> constructor) {
        return RecordCodecBuilder.create(inst -> inst.group(
                Codec.floatRange(0.0F, 1.0F).fieldOf("size").forGetter(sizeFunction)
        ).apply(inst, constructor));
    }

    public static <T extends RagingParticleOptions> Deserializer<T> createDeserializer(Function<Float, T> constructor) {
        return new Deserializer<>() {
            @Override
            public T fromCommand(ParticleType<T> type, StringReader reader) throws CommandSyntaxException {
                float size = reader.readFloat();
                return constructor.apply(size);
            }

            @Override
            public T fromNetwork(ParticleType<T> type, FriendlyByteBuf buf) {
                return constructor.apply(buf.readFloat());
            }
        };
    }

    public static class StageOne extends RagingParticleOptions {
        public static final Codec<StageOne> CODEC = createCodec(RagingParticleOptions::size, StageOne::new);
        public static final Deserializer<StageOne> DESERIALIZER = createDeserializer(StageOne::new);

        public StageOne(float size) {
            super(size);
        }

        @Override
        public ParticleType<?> getType() {
            return BnCParticleTypes.RAGING_STAGE_1.get();
        }
    }

    public static class StageTwo extends RagingParticleOptions {
        public static final Codec<StageTwo> CODEC = createCodec(StageTwo::size, StageTwo::new);
        public static final Deserializer<StageTwo> DESERIALIZER = createDeserializer(StageTwo::new);

        public StageTwo(float size) {
            super(size);
        }

        @Override
        public ParticleType<?> getType() {
            return BnCParticleTypes.RAGING_STAGE_2.get();
        }
    }

    public static class StageThree extends RagingParticleOptions {
        public static final Codec<StageThree> CODEC = createCodec(StageThree::size, StageThree::new);
        public static final Deserializer<StageThree> DESERIALIZER = createDeserializer(StageThree::new);

        public StageThree(float size) {
            super(size);
        }

        @Override
        public ParticleType<?> getType() {
            return BnCParticleTypes.RAGING_STAGE_3.get();
        }
    }

    public static class StageFour extends RagingParticleOptions {
        public static final Codec<StageFour> CODEC = createCodec(StageFour::size, StageFour::new);
        public static final Deserializer<StageFour> DESERIALIZER = createDeserializer(StageFour::new);

        public StageFour(float size) {
            super(size);
        }

        @Override
        public ParticleType<?> getType() {
            return BnCParticleTypes.RAGING_STAGE_4.get();
        }
    }
}
