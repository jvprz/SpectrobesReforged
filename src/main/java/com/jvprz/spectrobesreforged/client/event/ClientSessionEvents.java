package com.jvprz.spectrobesreforged.client.event;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.network.SyncedGameData;
import com.jvprz.spectrobesreforged.client.feature.prizmod.ClientPrizmodState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import java.util.List;

@EventBusSubscriber(modid=SpectrobesReforged.MODID,value=Dist.CLIENT)
public final class ClientSessionEvents {
    private ClientSessionEvents() {}
    // Beta-only local notice. Login fires once per connection, not on respawn or dimension travel.
    @SubscribeEvent public static void login(ClientPlayerNetworkEvent.LoggingIn event) {
        var mc=net.minecraft.client.Minecraft.getInstance();
        mc.gui.getChat().addMessage(net.minecraft.network.chat.Component.literal("[Spectrobes Reforged] ")
            .withStyle(net.minecraft.ChatFormatting.AQUA)
            .append(net.minecraft.network.chat.Component.translatable("spectrobesreforged.ui.betaFeedback").withStyle(net.minecraft.ChatFormatting.GRAY))
            .append(" ").append(net.minecraft.network.chat.Component.translatable("spectrobesreforged.ui.discord")
                .withStyle(style->style.withColor(net.minecraft.ChatFormatting.AQUA).withUnderlined(true)
                    .withClickEvent(new net.minecraft.network.chat.ClickEvent(net.minecraft.network.chat.ClickEvent.Action.OPEN_URL,"https://discord.gg/7QkScUWBe9")))));
    }
    @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event){
        SyncedGameData.clear();ClientPrizmodState.setSnapshot(List.of(),List.of(),null);
    }
}
