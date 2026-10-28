package com.jvprz.spectrobesreforged.common.network;

import net.minecraft.server.level.ServerPlayer;

public final class PlayerRequestLimits {
    private PlayerRequestLimits() {}
    public static boolean allow(ServerPlayer player, String action, int ticks) {
        long now=player.serverLevel().getGameTime();
        var data=player.getPersistentData();
        String key="SpectrobesRequest_"+action;
        long last=data.getLong(key);
        if(data.contains(key)&&now>=last&&now-last<ticks)return false;
        data.putLong(key,now);return true;
    }
}
