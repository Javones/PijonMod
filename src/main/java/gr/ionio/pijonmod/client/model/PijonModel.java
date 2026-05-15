package gr.ionio.pijonmod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gr.ionio.pijonmod.entity.Pijon;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class PijonModel<T extends Pijon> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("pijonmod", "pijon"), "main");

	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart left_wing;
	private final ModelPart left_wing_rotation;
	private final ModelPart right_wing;
	private final ModelPart right_wing_rotation;
	private final ModelPart left_leg;
	private final ModelPart right_leg;
	private final ModelPart tail;

	public PijonModel(ModelPart root) {
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.left_wing = root.getChild("left_wing");
		this.left_wing_rotation = this.left_wing.getChild("left_wing_rotation");
		this.right_wing = root.getChild("right_wing");
		this.right_wing_rotation = this.right_wing.getChild("right_wing_rotation");
		this.left_leg = root.getChild("left_leg");
		this.right_leg = root.getChild("right_leg");
		this.tail = root.getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 2).addBox(-1.5F, -2.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(12, 2).addBox(-1.5F, -3.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(16, 7).addBox(-1.0F, -2.1F, -1.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 16.0F, -0.5F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 7).addBox(-2.5F, 0.0F, -1.5F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.5F, -1.0F));

		PartDefinition left_wing = partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offset(1.5F, 16.9F, -0.8F));

		PartDefinition left_wing_rotation = left_wing.addOrReplaceChild("left_wing_rotation", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5F, -2.5F, -1.5F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition right_wing = partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offset(-1.5F, 16.9F, -0.8F));

		PartDefinition right_wing_rotation = right_wing.addOrReplaceChild("right_wing_rotation", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5F, -2.5F, -1.5F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(14, 18).addBox(-3.0F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 22.0F, -1.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(14, 18).addBox(1.0F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 22.0F, -1.0F));

		PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(22, 1).addBox(-2.0F, -1.0F, 0.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.1F, 1.2F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);

		if (!entity.onGround()) {
			this.head.z = -0.5F;

			float flyPitch = 0.5F;
			this.body.xRot = flyPitch;
			this.tail.xRot = flyPitch;
			this.left_wing.xRot = flyPitch;
			this.right_wing.xRot = flyPitch;

			this.head.xRot -= flyPitch / 2.0F;

			this.left_wing.zRot = (float)Math.cos(ageInTicks * 1.2F) * 0.6F;
			this.right_wing.zRot = -(float)Math.cos(ageInTicks * 1.2F) * 0.6F;
		} else {
			this.head.z = -0.5F - (float)Math.sin(limbSwing * 2.0F) * limbSwingAmount * 1.5F;

			this.body.xRot = 0.0F;
			this.tail.xRot = 0.0F;
			this.left_wing.xRot = 0.0F;
			this.right_wing.xRot = 0.0F;

			this.left_wing.zRot = 0.0F;
			this.right_wing.zRot = 0.0F;
		}

		this.left_leg.xRot = (float)Math.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.right_leg.xRot = (float)Math.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;

		// --- Η ΔΙΟΡΘΩΜΕΝΗ ΛΟΓΙΚΗ ΓΙΑ ΤΟ ΚΑΘΙΣΜΑ ---
		if (entity.isInSittingPose()) {
			// Κατεβάζουμε σταθερά ΟΛΑ τα κομμάτια κατά 2.0 pixels
			this.head.y = 16.0F + 2.0F;
			this.body.y = 16.5F + 2.0F;
			this.left_wing.y = 16.9F + 2.0F;
			this.right_wing.y = 16.9F + 2.0F;
			this.tail.y = 21.1F + 2.0F;

			// Κρύβουμε τα πόδια
			this.right_leg.visible = false;
			this.left_leg.visible = false;
		} else {
			// Επαναφορά στην αρχική τους θέση όταν σηκώνεται
			this.head.y = 16.0F;
			this.body.y = 16.5F;
			this.left_wing.y = 16.9F;
			this.right_wing.y = 16.9F;
			this.tail.y = 21.1F;

			// Εμφανίζουμε πάλι τα πόδια
			this.right_leg.visible = true;
			this.left_leg.visible = true;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		left_wing.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		right_wing.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		tail.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}