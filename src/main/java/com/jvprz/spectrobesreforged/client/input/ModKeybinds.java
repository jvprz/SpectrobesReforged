package com.jvprz.spectrobesreforged.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class ModKeybinds {

    private ModKeybinds() {}

    public static final String CATEGORY = "key.categories.spectrobesreforged";

    public static final KeyMapping RANGE_SCAN = new KeyMapping(
            "key.spectrobesreforged.range_scan",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            CATEGORY
    );
}