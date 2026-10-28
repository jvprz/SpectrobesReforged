package com.jvprz.spectrobesreforged.common.registry;
import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.worldgen.ExcavationPatchFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class ModFeatures {
 private ModFeatures(){}
 private static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(Registries.FEATURE,SpectrobesReforged.MODID);
 public static final java.util.function.Supplier<ExcavationPatchFeature> EXCAVATION_PATCH=FEATURES.register("excavation_patch",ExcavationPatchFeature::new);
 public static void register(IEventBus bus){FEATURES.register(bus);}
}
