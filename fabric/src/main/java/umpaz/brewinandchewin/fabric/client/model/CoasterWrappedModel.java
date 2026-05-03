package umpaz.brewinandchewin.fabric.client.model;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CoasterWrappedModel implements BakedModel {
    private final BakedModel model;
    private int tintIndex;

    public CoasterWrappedModel(BakedModel originalModel) {
        this.model = originalModel;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    public void setTintIndex(int tintIndex) {
        this.tintIndex = tintIndex;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        QuadEmitter emitter = context.getEmitter();

        for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
            final Direction cullFace = ModelHelper.faceFromIndex(i);

            if (!context.hasTransform() && context.isFaceCulled(cullFace))
                continue;

            final List<BakedQuad> quads = model.getQuads(state, cullFace, randomSupplier.get());

            for (final BakedQuad q : quads) {
                q.tintIndex = tintIndex;
                emitter.fromVanilla(q, context.getEmitter().material(), cullFace);
                emitter.emit();
            }
        }

        setTintIndex(-1);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
        return model.getQuads(blockState, direction, randomSource);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return model.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return model.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return model.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return model.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return model.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return model.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return model.getOverrides();
    }
}
