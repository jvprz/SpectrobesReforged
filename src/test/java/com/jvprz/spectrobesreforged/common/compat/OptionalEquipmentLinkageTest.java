package com.jvprz.spectrobesreforged.common.compat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OptionalEquipmentLinkageTest {
    @Test void baseItemsAndInputHandlersLinkWithTheSelectedRuntimeClasspath()throws Exception{
        var loader=getClass().getClassLoader();
        assertEquals(Boolean.getBoolean("spectrobes.tests.curios"),loader.getResource("top/theillusivec4/curios/api/CuriosApi.class")!=null,
                "The no-Curios run must really exclude the library, not merely disable a loaded mod");
        for(String name:new String[]{
                "com.jvprz.spectrobesreforged.common.content.item.PrizmodItem",
                "com.jvprz.spectrobesreforged.common.feature.prizmod.PrizmodAccess",
                "com.jvprz.spectrobesreforged.client.event.ClientInputEvents"}){
            var type=Class.forName(name,false,loader);
            assertDoesNotThrow(()->type.getDeclaredMethods(),name);
            assertDoesNotThrow(()->type.getInterfaces(),name);
        }
    }
}
