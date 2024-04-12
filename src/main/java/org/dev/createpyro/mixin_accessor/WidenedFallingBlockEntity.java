package org.dev.createpyro.mixin_accessor;

import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface WidenedFallingBlockEntity {
    void initWithVelocity(double x, double y, double z, BlockState state, Vec3 velocity);
}
