package gr.ionio.pijonmod.client.renderer;

import gr.ionio.pijonmod.entity.Pijon;
import gr.ionio.pijonmod.client.model.PijonModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PijonRenderer extends MobRenderer<Pijon, PijonModel<Pijon>> {
    public PijonRenderer(EntityRendererProvider.Context context) {
        super(context, new PijonModel<>(context.bakeLayer(PijonModel.LAYER_LOCATION)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(Pijon entity) {
        String variantName = entity.getVariant().getSerializedName();

        return ResourceLocation.fromNamespaceAndPath("pijonmod", "textures/entity/pijon/pijon_" + variantName + ".png");
    }
}
