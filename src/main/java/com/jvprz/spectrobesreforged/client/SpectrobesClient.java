package com.jvprz.spectrobesreforged.client;

import com.jvprz.spectrobesreforged.content.item.TypeTooltipComponent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

public final class SpectrobesClient {
    private SpectrobesClient() {}

    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(TypeTooltipComponent.class, TypeTooltipRenderer::new);
    }
}