package org.teamvoided.world_foundry.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import static org.teamvoided.world_foundry.WorldFoundry.log;

@Mixin(DarkOakTrunkPlacer.class)
public class DarkOakTrunkPlacerMixin {

    @WrapOperation(method = "placeTrunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/TreeFeature;isAirOrLeaves(Lnet/minecraft/world/level/LevelSimulatedReader;Lnet/minecraft/core/BlockPos;)Z"))
    boolean useTheFrickenCorrectCall(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos, Operation<Boolean> original) {
            log.info("{}", blockPos);
        return TreeFeature.isAirOrLeaves(levelSimulatedReader, blockPos);
    }
}
