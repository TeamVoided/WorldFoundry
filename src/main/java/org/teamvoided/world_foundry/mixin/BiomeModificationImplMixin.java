
package org.teamvoided.world_foundry.mixin;

import net.fabricmc.fabric.impl.biome.modification.BiomeModificationImpl;
import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teamvoided.world_foundry.worldgen.FeatureModifier;

@Mixin(BiomeModificationImpl.class)
public abstract class BiomeModificationImplMixin {

    @Inject(method = "finalizeWorldGen", at = @At(value = "TAIL"), order = 10_000)
    private void afterBiomeModify(RegistryAccess impl, CallbackInfo ci) {
        FeatureModifier.featureModificationStep(impl);
    }

}
