package org.teamvoided.world_foundry.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RepeatingPlacement.class)
public interface RepeatingPlacementAccessor {
    @Invoker("count")
    int wf_count(RandomSource randomSource, BlockPos blockPos);
}
