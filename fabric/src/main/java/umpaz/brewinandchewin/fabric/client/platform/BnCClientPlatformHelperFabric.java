package umpaz.brewinandchewin.fabric.client.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import umpaz.brewinandchewin.platform.client.BnCClientPlatformHelper;

public class BnCClientPlatformHelperFabric implements BnCClientPlatformHelper {
    @Override
    public BakedModel getModel(ResourceLocation modelId) {
        return Minecraft.getInstance().getModelManager().getModel(modelId);
    }

    @Override
    public void tesselateCoasterModel(BlockAndTintGetter level, ResourceLocation modelId, BlockState state, BlockPos pos, PoseStack poseStack, MultiBufferSource buffer, RandomSource random, long seed, int packedOverlay, int tintIndex, RenderType renderType) {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(level, Minecraft.getInstance().getModelManager().getModel(modelId), state, pos, poseStack, buffer.getBuffer(renderType), false, random, seed, packedOverlay);
    }
}
