package org.dev.createpyro.mixin_accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ItemDropFallingTracker {
    
    public static boolean HAS_GENERATED_SPAWNMAP = false;
    public static Explosion CURRENT_THREAD_EXPLOSION = null;
    public static BlockPos CURRENT_THREAD_BLOCKPOS = null;
    
    public static Level LEVEL;
    
}
