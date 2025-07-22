package com.iafenvoy.throwable.render;

import com.iafenvoy.throwable.object.ThrowableWeaponItem;
import com.iafenvoy.throwable.object.ThrownWeaponEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class ThrownWeaponEntityRenderer extends EntityRenderer<ThrownWeaponEntity> {
    private final ItemRenderer itemRenderer;

    public ThrownWeaponEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(ThrownWeaponEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        ItemStack stack = entity.asItemStack();
        matrices.scale(2, 2, 2);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) - 90));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch()) + (stack.getItem() instanceof ThrowableWeaponItem item ? item.getRotateOffset() : 0) - 135));
        matrices.translate(-0.125, 0, 0);
        this.itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getEntityWorld(), 0);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(ThrownWeaponEntity entity) {
        return RenderConstants.WHITE_TEXTURE;
    }

    public static void applyThrowTransform(MatrixStack matrices){
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
        matrices.translate(0, 0.25, 0);
    }
}
