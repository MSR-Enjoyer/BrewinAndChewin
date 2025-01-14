package umpaz.brewinandchewin.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.BnCConfiguration;
import umpaz.brewinandchewin.common.registry.BnCEffects;
import vectorwing.farmersdelight.common.registry.ModEffects;

import java.util.Random;

public class BnCHUDOverlays {
    protected static int foodIconsOffset;
    public static final ResourceLocation MOD_ICONS_TEXTURE = new ResourceLocation(BrewinAndChewin.MODID, "textures/gui/bnc_icons.png");
    private static final ResourceLocation NOURISHMENT_ICONS_TEXTURE = new ResourceLocation("farmersdelight", "textures/gui/fd_icons.png");

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new BnCHUDOverlays());
    }

    private static final ResourceLocation NAUSEA_LOCATION = new ResourceLocation("textures/misc/nausea.png");
    private static final ResourceLocation PORTAL_ELEMENT = new ResourceLocation("minecraft", "portal");
    private static final ResourceLocation FOOD_LEVEL_ELEMENT = new ResourceLocation("minecraft", "food_level");

    private static float tipsyTransparencyModifier = 0.0F;

    @SubscribeEvent
    public void onRenderGuiOverlayPost(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        ForgeGui gui = (ForgeGui) mc.gui;

        if (mc.player == null)
            return;

        if (event.getOverlay() == GuiOverlayManager.findOverlay(PORTAL_ELEMENT)) {
            if (!mc.player.hasEffect(MobEffects.CONFUSION) && mc.player.hasEffect(BnCEffects.TIPSY.get())) {
                MobEffectInstance effect = mc.player.getEffect(BnCEffects.TIPSY.get());
                float distortionScale = mc.options.screenEffectScale().get().floatValue();
                float tipsyScale = Math.min((1 + effect.getAmplifier()) / 10.0F * 0.4F, 0.4F);
                if (distortionScale < 1.0F && tipsyScale > 0.0F) {
                    renderTipsyOverlay(event.getGuiGraphics(), (1.0F - distortionScale) * tipsyScale * tipsyTransparencyModifier);
                    float partialTickModifier = event.getPartialTick() * (effect.endsWithin(60) ? -0.006F : 0.007F);
                    tipsyTransparencyModifier = Mth.clamp(tipsyTransparencyModifier + partialTickModifier, 0.0F, 1.0F);
                } else
                    tipsyTransparencyModifier = 0.0F;
            } else
                tipsyTransparencyModifier = 0.0F;
        }

        if (event.getOverlay() == GuiOverlayManager.findOverlay(FOOD_LEVEL_ELEMENT) &&
                !mc.options.hideGui && gui.shouldDrawSurvivalElements() &&
                mc.player.hasEffect(BnCEffects.INTOXICATION.get()))
            renderIntoxicationOverlay(gui, event.getGuiGraphics());
    }

    public static void renderTipsyOverlay(GuiGraphics guiGraphics, float scalar) {
        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        guiGraphics.setColor(scalar, 0.55F * scalar, 0.08F * scalar, 1.0F);
        guiGraphics.blit(NAUSEA_LOCATION, 0, 0, -90, 0.0F, 0.0F, width, height, width, height);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    public static void renderIntoxicationOverlay(ForgeGui gui, GuiGraphics guiGraphics) {
        if (!BnCConfiguration.INTOXICATION_FOOD_OVERLAY.get()) {
            return;
        }

        foodIconsOffset = gui.rightHeight;
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null) {
            return;
        }
        int top = minecraft.getWindow().getGuiScaledHeight() - foodIconsOffset;
        int right = minecraft.getWindow().getGuiScaledWidth() / 2 + 91;

        drawIntoxicationOverlay(player, minecraft, guiGraphics, right, top);
    }

    public static void drawIntoxicationOverlay(Player player, Minecraft minecraft, GuiGraphics graphics, int right, int top) {
        int ticks = minecraft.gui.getGuiTicks();
        Random rand = new Random();
        rand.setSeed(ticks * 312871L);

        RenderSystem.enableBlend();

        ResourceLocation texture = player.hasEffect(ModEffects.NOURISHMENT.get()) ? NOURISHMENT_ICONS_TEXTURE : MOD_ICONS_TEXTURE;

        for (int i = 0; i < 10; ++i) {
            int x = (right - i * 8 - 9) + (int) (Mth.cos((ticks + i * 2) * 0.20F) * 2f);
            int y = (top) + (int) (Mth.sin((ticks + i * 2) * 0.25F) * 2f);

            float effectiveHungerOfBar = (float) player.getFoodData().getFoodLevel() / 2.0F - (float) i;
            boolean isPlayerHealingWithSaturationAndNourishment =
                    player.hasEffect(ModEffects.NOURISHMENT.get()) &&
                            player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)
                            && player.isHurt()
                            && player.getFoodData().getFoodLevel() >= 18;
            int naturalHealingOffset = isPlayerHealingWithSaturationAndNourishment ? 18 : 0;

            graphics.blit(texture, x, y, 0, 0, 9, 9);

            if (effectiveHungerOfBar >= 1.0F) {
                graphics.blit(texture, x, y, 18 + naturalHealingOffset, 0, 9, 9);
            } else if ((double) effectiveHungerOfBar >= (double) 0.5F) {
                graphics.blit(texture, x, y, 9 + naturalHealingOffset, 0, 9, 9);
            }
        }

        RenderSystem.disableBlend();
    }
}