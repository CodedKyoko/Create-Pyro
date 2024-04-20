package org.dev.createpyro.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RedButtonBlock extends ButtonBlock {
    protected static final VoxelShape CEILING_AABB;
    protected static final VoxelShape FLOOR_AABB;
    protected static final VoxelShape NORTH_AABB;
    protected static final VoxelShape SOUTH_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape EAST_AABB;
    protected static final VoxelShape PRESSED_CEILING_AABB;
    protected static final VoxelShape PRESSED_FLOOR_AABB;
    protected static final VoxelShape PRESSED_NORTH_AABB;
    protected static final VoxelShape PRESSED_SOUTH_AABB;
    protected static final VoxelShape PRESSED_WEST_AABB;
    protected static final VoxelShape PRESSED_EAST_AABB;
    private final int ticksToStayPressed;

    public RedButtonBlock(Properties properties, BlockSetType type, int ticksToStayPressed, boolean arrowsCanPress) {
        super(properties, type, ticksToStayPressed, arrowsCanPress);
        this.ticksToStayPressed = ticksToStayPressed;
    }

    public void press(BlockState state, Level level, @NotNull BlockPos pos) {
        level.setBlock(pos, (BlockState)state.setValue(POWERED, true), 3);
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), this);
        level.scheduleTick(pos, this, this.ticksToStayPressed);
        igniteGunpowder(level, pos, state);
    }

    public void igniteGunpowder(Level level, BlockPos pos, BlockState state){
        List<BlockPos> positions = new ArrayList<BlockPos>();
        Direction[] directions = Direction.values();
        for (Direction direction : directions){
            positions.add(pos.relative(direction));
        }

        for (Direction direction : directions){
            positions.add(pos.relative(getConnectedDirection(state).getOpposite()).relative(direction));
        }

        for (BlockPos position : positions){
            if (level.getBlockState(position).getBlock() instanceof GunPowderWireBlock wire){
                wire.ignite(level, position, level.getBlockState(position));
            }
        }
    }

    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {

        Direction direction = (Direction)state.getValue(FACING);
        boolean bl = (Boolean)state.getValue(POWERED);
        switch ((AttachFace)state.getValue(FACE)) {
            case FLOOR:
                return bl ? PRESSED_FLOOR_AABB : FLOOR_AABB;
            case WALL:
                VoxelShape var10000;
                switch (direction) {
                    case EAST:
                        var10000 = bl ? PRESSED_EAST_AABB : EAST_AABB;
                        break;
                    case WEST:
                        var10000 = bl ? PRESSED_WEST_AABB : WEST_AABB;
                        break;
                    case SOUTH:
                        var10000 = bl ? PRESSED_SOUTH_AABB : SOUTH_AABB;
                        break;
                    case NORTH:
                    case UP:
                    case DOWN:
                        var10000 = bl ? PRESSED_NORTH_AABB : NORTH_AABB;
                        break;
                    default:
                        throw new IncompatibleClassChangeError();
                }

                return var10000;
            case CEILING:
            default:
                return bl ? PRESSED_CEILING_AABB : CEILING_AABB;
        }
    }

    static {
        CEILING_AABB = Block.box(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
        FLOOR_AABB = Block.box(4.0, 0.0, 4.0, 12.0, 3.0, 12.0);
        NORTH_AABB = Block.box(4.0, 4.0, 13.0, 12.0, 12.0, 16.0);
        SOUTH_AABB = Block.box(4.0, 4.0, 0.0, 12.0, 12.0, 3.0);
        WEST_AABB = Block.box(13.0, 4.0, 4.0, 16.0, 12.0, 12.0);
        EAST_AABB = Block.box(0.0, 4.0, 4.0, 3.0, 12.0, 12.0);
        PRESSED_CEILING_AABB = Block.box(4.0, 14.0, 4.0, 12.0, 16.0, 12.0);
        PRESSED_FLOOR_AABB = Block.box(4.0, 0.0, 4.0, 12.0, 2.0, 12.0);
        PRESSED_NORTH_AABB = Block.box(4.0, 4.0, 14.0, 12.0, 12.0, 16.0);
        PRESSED_SOUTH_AABB = Block.box(4.0, 4.0, 0.0, 12.0, 12.0, 2.0);
        PRESSED_WEST_AABB = Block.box(14.0, 4.0, 4.0, 16.0, 12.0, 12.0);
        PRESSED_EAST_AABB = Block.box(0.0, 4.0, 4.0, 2.0, 12.0, 12.0);
    }
}
