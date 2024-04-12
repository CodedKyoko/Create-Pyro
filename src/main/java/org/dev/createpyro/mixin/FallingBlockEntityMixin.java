package org.dev.createpyro.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.dev.createpyro.mixin_accessor.WidenedFallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity implements WidenedFallingBlockEntity {
    
    @Shadow public abstract void setStartPos(BlockPos startPos);
    /**Shadow*/
    public FallingBlockEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    
    @Shadow private BlockState blockState;
    
    @Override
    public void initWithVelocity(double x, double y, double z, BlockState state, Vec3 velocity) {
        this.blockState = state;
        this.blocksBuilding = true;
        setPos(x, y, z);
        setDeltaMovement(velocity);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
    }
    
    @Shadow
    protected void defineSynchedData() {}
    @Shadow
    protected void readAdditionalSaveData(CompoundTag compound) {}
    @Shadow
    protected void addAdditionalSaveData(CompoundTag compound) {}
    
}
