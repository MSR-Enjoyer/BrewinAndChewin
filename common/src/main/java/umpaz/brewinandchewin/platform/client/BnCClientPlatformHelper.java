package umpaz.brewinandchewin.platform.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface BnCClientPlatformHelper {
    BakedModel getModel(ResourceLocation modelId);

    void tesselateCoasterModel(BlockAndTintGetter level,
                               ResourceLocation modelId,
                               BlockState state,
                               BlockPos pos,
                               PoseStack poseStack,
                               MultiBufferSource buffer,
                               RandomSource random,
                               long seed,
                               int packedOverlay,
                               int tintIndex,
                               RenderType renderType);
}
