package gr.ionio.pijonmod.client;

import gr.ionio.pijonmod.init.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffects;

public class StinkOverlay {
    private static final ResourceLocation STINK_TEXTURE = ResourceLocation.parse("pijonmod:textures/gui/stink_overlay.png");

    public static void renderStink() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player != null && minecraft.player.hasEffect(ModEffects.STINK.getHolder().get())) {
            GuiGraphics guiGraphics = new GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource());
            int width = minecraft.getWindow().getGuiScaledWidth();
            int height = minecraft.getWindow().getGuiScaledHeight();

            RenderSystem.enableBlend();
            guiGraphics.blit(STINK_TEXTURE, 0, 0, -90, 0.0F, 0.0F, width, height, width, height);
            RenderSystem.disableBlend();
        }
    }
}