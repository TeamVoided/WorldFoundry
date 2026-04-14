package org.teamvoided.world_foundry.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teamvoided.world_foundry.worldgen.FeatureModifier;

@Mixin(PlacedFeature.class)
public class PlacedFeatureMixin {

    @Inject(method = "placeWithContext", at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/mutable/MutableBoolean;isTrue()Z"), cancellable = true, order = 1100)
    void additionalPlacements(PlacementContext placementContext, RandomSource randomSource, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir, @Local MutableBoolean placed) {
        FeatureModifier.customPlaceFeature((PlacedFeature) (Object) this, placementContext, randomSource, blockPos, cir, placed);
    }
}
