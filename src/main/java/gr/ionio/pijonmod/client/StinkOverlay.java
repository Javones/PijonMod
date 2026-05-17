package gr.ionio.pijonmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;

public class StinkOverlay {
    private static final ResourceLocation STINK_TEXTURE = ResourceLocation.fromNamespaceAndPath("pijonmod", "textures/gui/stink_overlay.png");

    public static void renderStink(GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player != null && minecraft.player.hasEffect(gr.ionio.pijonmod.init.ModEffects.STINK.getHolder().get())) {

            int width = minecraft.getWindow().getGuiScaledWidth();
            int height = minecraft.getWindow().getGuiScaledHeight();

            // Κρατάμε τα χρώματα στο 100% φωτεινότητα
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();

            guiGraphics.blit(STINK_TEXTURE, 0, 0, 0, 0, width, height, width, height);

            RenderSystem.disableBlend();
        }
    }
}