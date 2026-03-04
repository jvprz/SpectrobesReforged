package com.jvprz.spectrobesreforged.common.registry;

import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class ModEvents {

    private ModEvents() {}

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }
}