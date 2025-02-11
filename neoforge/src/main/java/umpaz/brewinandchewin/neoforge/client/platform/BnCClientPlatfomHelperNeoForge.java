package umpaz.brewinandchewin.neoforge.client.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import umpaz.brewinandchewin.neoforge.client.model.CoasterWrappedModel;
import umpaz.brewinandchewin.platform.client.BnCClientPlatformHelper;

public class BnCClientPlatfomHelperNeoForge implements BnCClientPlatformHelper {
    @Override
    public BakedModel getModel(ResourceLocation modelId) {
        return Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(modelId));
    }

    @Override
    public void tesselateCoasterModel(BlockAndTintGetter level, ResourceLocation modelId, BlockState state, BlockPos pos, PoseStack poseStack, MultiBufferSource buffer, RandomSource random, long seed, int packedOverlay, int tintIndex, RenderType renderType) {
        ModelData data = ModelData.EMPTY;
        if (tintIndex != -1) {
            data = ModelData.builder()
                    .with(CoasterWrappedModel.TINT_INDEX, tintIndex)
                    .build();
        }
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(level, Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(modelId)), state, pos, poseStack, buffer.getBuffer(renderType), false, random, seed, packedOverlay, data, renderType);
    }
}
