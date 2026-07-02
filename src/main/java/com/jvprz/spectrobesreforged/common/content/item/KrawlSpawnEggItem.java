package com.jvprz.spectrobesreforged.common.content.item;

import com.jvprz.spectrobesreforged.common.content.entity.KrawlEntity;
import com.jvprz.spectrobesreforged.common.factory.KrawlFactory;
import com.jvprz.spectrobesreforged.common.feature.krawl.KrawlDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class KrawlSpawnEggItem extends Item {

    private final KrawlDefinition definition;

    public KrawlSpawnEggItem(KrawlDefinition definition, Properties properties) {
        super(properties);
        this.definition = definition;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());

        KrawlEntity entity = KrawlFactory.create(level, definition);

        entity.moveTo(
                pos.getX() + 0.5D,
                pos.getY(),
                pos.getZ() + 0.5D,
                context.getPlayer() != null ? context.getPlayer().getYRot() : 0.0F,
                0.0F
        );

        level.addFreshEntity(entity);

        if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}