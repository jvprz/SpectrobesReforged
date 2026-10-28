package com.jvprz.spectrobesreforged.common.gametest;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.compat.OptionalEquipment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

@EventBusSubscriber(modid=SpectrobesReforged.MODID)
public final class OptionalGameTests {
    private OptionalGameTests() {}
    @SubscribeEvent public static void register(RegisterGameTestsEvent event){
        if(OptionalEquipment.hasCurios())event.register(com.jvprz.spectrobesreforged.common.compat.curios.CuriosEquipmentTests.class);
    }
}
