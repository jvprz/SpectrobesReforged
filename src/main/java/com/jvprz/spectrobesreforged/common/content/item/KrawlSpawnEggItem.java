package com.jvprz.spectrobesreforged.common.content.item;

import com.jvprz.spectrobesreforged.common.content.entity.krawl.KrawlEntity;
import com.jvprz.spectrobesreforged.common.feature.krawl.factory.KrawlFactory;
import com.jvprz.spectrobesreforged.common.feature.krawl.KrawlRegistry;
import com.jvprz.spectrobesreforged.common.feature.krawl.data.KrawlDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class KrawlSpawnEggItem extends Item {

    private final String krawlKey;

    public KrawlSpawnEggItem(String krawlKey, Properties properties) {
        super(properties);
        this.krawlKey = krawlKey;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        KrawlDefinition definition = KrawlRegistry.getByKey(krawlKey);

        if (definition == null) {
            return InteractionResult.FAIL;
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