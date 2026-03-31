package org.teamvoided.world_foundry.mixin.datagen;

import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.ToFloatFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teamvoided.world_foundry.datagen.MixinFunctions;

@Mixin(TerrainProvider.class)
public abstract class TerrainProviderMixin {

    @Shadow
    public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildErosionOffsetSpline(I locationFunction, I toFloatFunction, float f, float g, float h, float i, float j, float k, boolean bl, boolean bl2, ToFloatFunction<Float> toFloatFunction2) {
        return null;
    }

    @Shadow
    @Final
    private static ToFloatFunction<Float> AMPLIFIED_OFFSET;

    @Shadow
    @Final
    private static ToFloatFunction<Float> NO_TRANSFORM;

    @Inject(method = "overworldOffset", at = @At("RETURN"), cancellable = true)
    private static <C, I extends ToFloatFunction<C>> void offsetSplineWithDeeperOcean(I toFloatFunction, I toFloatFunction2, I toFloatFunction3, boolean bl, CallbackInfoReturnable<CubicSpline<C, I>> cir) {
        ToFloatFunction<Float> toFloatFunction4 = bl ? AMPLIFIED_OFFSET : NO_TRANSFORM;
        var mushroomIslandSpline = MixinFunctions.oceanFloorSpline(toFloatFunction3, -0.075F, 0.04F, 0.048F, 0.088F, 0.1F, toFloatFunction4);
        var deeperOceanSpline = MixinFunctions.oceanFloorSpline(toFloatFunction3, -0.066F, -0.56F, -0.52F, -0.48F, -0.44F, toFloatFunction4);
        var deepOceanSpline = MixinFunctions.oceanFloorSpline(toFloatFunction3, -0.56F, -0.41F, -0.4F, -0.36F, -0.33F, toFloatFunction4);
        var oceanSpline = MixinFunctions.oceanFloorSpline(toFloatFunction3, -0.45F, -0.31F, -0.3F, -0.27F, -0.22F, toFloatFunction4);
        var shallowOceanSpline = MixinFunctions.oceanFloorSpline(toFloatFunction3, -0.33F, -0.23F, -0.2F, -0.155F, -0.11F, toFloatFunction4);
        var shorelineSpline = buildErosionOffsetSpline(toFloatFunction2, toFloatFunction3, -0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false, toFloatFunction4);
        var outlandSpline = buildErosionOffsetSpline(toFloatFunction2, toFloatFunction3, -0.1F, 0.03F, 0.1F, 0.1F, 0.01F, -0.03F, false, false, toFloatFunction4);
        var midlandSpline = buildErosionOffsetSpline(toFloatFunction2, toFloatFunction3, -0.1F, 0.03F, 0.1F, 0.7F, 0.01F, -0.03F, true, true, toFloatFunction4);
        var inlandSpline = buildErosionOffsetSpline(toFloatFunction2, toFloatFunction3, -0.05F, 0.03F, 0.1F, 1.0F, 0.01F, 0.01F, true, true, toFloatFunction4);
        cir.setReturnValue(
                CubicSpline.builder(toFloatFunction, toFloatFunction4)
                        .addPoint(-1.1F, mushroomIslandSpline)
                        .addPoint(-1.02F, deeperOceanSpline)
                        .addPoint(-0.51F, deepOceanSpline)
                        .addPoint(-0.44F, oceanSpline)
                        .addPoint(-0.18F, shallowOceanSpline)
                        .addPoint(-0.16F, shorelineSpline)
                        .addPoint(-0.15F, shorelineSpline)
                        .addPoint(-0.1F, outlandSpline)
                        .addPoint(0.25F, midlandSpline)
                        .addPoint(1.0F, inlandSpline)
                        .build()
        );
    }
}
