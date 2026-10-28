package com.jvprz.spectrobesreforged.client.render;

import com.jvprz.spectrobesreforged.common.content.block.ExcavationTableBlock;
import com.jvprz.spectrobesreforged.common.content.block.entity.ExcavationTableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;

/** Flat, stationary specimen inside the 12x12 recessed work area, like an item frame. */
public final class ExcavationTableRenderer implements BlockEntityRenderer<ExcavationTableBlockEntity> {
    private final ItemRenderer items;
    public ExcavationTableRenderer(BlockEntityRendererProvider.Context context) { items = context.getItemRenderer(); }
    @Override public void render(ExcavationTableBlockEntity table, float partial, PoseStack pose,
            MultiBufferSource buffers, int light, int overlay) {
        var stack = table.displayStack();
        if (stack.isEmpty() || table.getLevel() == null) return;
        pose.pushPose();
        pose.translate(.5, 14.0 / 16 + .012, .5);
        pose.mulPose(Axis.YP.rotationDegrees(-table.getBlockState().getValue(ExcavationTableBlock.FACING).toYRot()));
        pose.mulPose(Axis.XP.rotationDegrees(90));
        pose.scale(.68F, .68F, .68F);
        int surfaceLight = net.minecraft.client.renderer.LevelRenderer.getLightColor(table.getLevel(), table.getBlockPos().above());
        items.renderStatic(stack, ItemDisplayContext.FIXED, surfaceLight, overlay, pose, buffers, table.getLevel(), 0);
        pose.popPose();
    }
}
