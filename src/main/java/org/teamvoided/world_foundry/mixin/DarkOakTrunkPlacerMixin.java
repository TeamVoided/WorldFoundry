package org.teamvoided.world_foundry.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DarkOakTrunkPlacer.class)
public class DarkOakTrunkPlacerMixin {

    @ModifyExpressionValue(method = "placeTrunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/TreeFeature;isAirOrLeaves(Lnet/minecraft/world/level/LevelSimulatedReader;Lnet/minecraft/core/BlockPos;)Z"))
    boolean useTheFrickenCorrectCall(boolean original) {
        return true;
    }
}
