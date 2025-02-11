package umpaz.brewinandchewin.neoforge.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.BrewinAndChewinClient;
import umpaz.brewinandchewin.common.BnCConfiguration;
import umpaz.brewinandchewin.common.registry.BnCEffects;
import vectorwing.farmersdelight.common.registry.ModEffects;

import java.util.Random;

@EventBusSubscriber(modid = BrewinAndChewin.MODID, bus = EventBusSubscriber.Bus.GAME)
public class BnCHUDOverlays {
    protected static int foodIconsOffset;
    public static final ResourceLocation MOD_ICONS_TEXTURE = BrewinAndChewin.asResource("textures/gui/bnc_icons.png");
    private static final ResourceLocation NOURISHMENT_ICONS_TEXTURE = ResourceLocation.fromNamespaceAndPath("farmersdelight", "textures/gui/fd_icons.png");

    private static final ResourceLocation NAUSEA_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/nausea.png");

    private static float tipsyTransparencyModifier = 0.0F;

    @SubscribeEvent(priority = EventPriority.LOW) // We want this to run after more important pre events.
    public static void onRenderGuiOverlayPost(RenderGuiLayerEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
            return;

        if (event.getName() == VanillaGuiLayers.CAMERA_OVERLAYS) {
            if (!mc.player.hasEffect(MobEffects.CONFUSION) && mc.player.hasEffect(BnCEffects.TIPSY)) {
                MobEffectInstance effect = mc.player.getEffect(BnCEffects.TIPSY);
                float distortionScale = mc.options.screenEffectScale().get().floatValue();
                float tipsyScale = Math.min((1 + effect.getAmplifier()) / 10.0F * 0.4F, 0.4F);
                if (distortionScale < 1.0F && tipsyScale > 0.0F) {
                    renderTipsyOverlay(event.getGuiGraphics(), (1.0F - distortionScale) * tipsyScale * tipsyTransparencyModifier);
                    float partialTickModifier = event.getPartialTick().getGameTimeDeltaTicks() * (effect.endsWithin(60) ? -0.006F : 0.007F);
                    tipsyTransparencyModifier = Mth.clamp(tipsyTransparencyModifier + partialTickModifier, 0.0F, 1.0F);
                } else
                    tipsyTransparencyModifier = 0.0F;
            } else
                tipsyTransparencyModifier = 0.0F;
        }

        if (event.getName() == VanillaGuiLayers.FOOD_LEVEL &&
                !mc.options.hideGui && mc.gameMode != null && mc.gameMode.canHurtPlayer() &&
                mc.player.hasEffect(BnCEffects.INTOXICATION)) {
            renderIntoxicationOverlay(mc.gui, event);
            event.setCanceled(true);
        }
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

    public static void renderIntoxicationOverlay(Gui gui, RenderGuiLayerEvent.Pre event) {
        if (!BnCConfiguration.CLIENT_CONFIG.get().intoxicationFoodOverlay()) {
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

        drawIntoxicationOverlay(player, minecraft, event.getGuiGraphics(), right, top);
        gui.rightHeight += 10;
    }

    public static void drawIntoxicationOverlay(Player player, Minecraft minecraft, GuiGraphics graphics, int right, int top) {
        int ticks = minecraft.gui.getGuiTicks();
        Random rand = new Random();
        rand.setSeed(ticks * 312871L);

        RenderSystem.enableBlend();

        ResourceLocation texture = player.hasEffect(ModEffects.NOURISHMENT) ? NOURISHMENT_ICONS_TEXTURE : MOD_ICONS_TEXTURE;

        for (int i = 0; i < 10; ++i) {
            int x = (right - i * 8 - 9) + (int) (Mth.cos((ticks + i * 2) * 0.20F) * 2f);
            int y = top + (int) (Mth.sin((ticks + i * 2) * 0.25F) * 2f);

            float effectiveHungerOfBar = (float) player.getFoodData().getFoodLevel() / 2.0F - (float) i;
            boolean isPlayerHealingWithSaturationAndNourishment =
                    player.hasEffect(ModEffects.NOURISHMENT) &&
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