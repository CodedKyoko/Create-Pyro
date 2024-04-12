package org.dev.createpyro.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dev.createpyro.registry.PyroBlocks;

import java.util.Iterator;
import java.util.Map;

public class GunPowderWireBlock extends Block {
    public static final IntegerProperty NORTH;
    public static final IntegerProperty EAST;
    public static final IntegerProperty SOUTH;
    public static final IntegerProperty WEST;
    public static final Map<Direction, IntegerProperty> PROPERTY_BY_DIRECTION;
    private static final VoxelShape SHAPE_DOT;
    private static final Map<Direction, VoxelShape> SHAPES_FLOOR;
    private static final Map<Direction, VoxelShape> SHAPES_UP;
    private static final Map<BlockState, VoxelShape> SHAPES_CACHE;
    private static final Vec3 COLOR;
    private final BlockState crossState;


    public GunPowderWireBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                (BlockState) ((BlockState) ((BlockState) ((BlockState) ((BlockState) this.stateDefinition.any())
                        .setValue(NORTH, 0))
                        .setValue(EAST, 0))
                        .setValue(SOUTH, 0))
                        .setValue(WEST, 0)
        );
        this.crossState = (BlockState)(
                (BlockState) ((BlockState) ((BlockState)this.defaultBlockState()
                        .setValue(NORTH, 1))
                        .setValue(EAST, 1))
                        .setValue(SOUTH, 1))
                        .setValue(WEST, 1);
        for (BlockState blockstate : this.getStateDefinition().getPossibleStates()) {
            SHAPES_CACHE.put(blockstate, this.calculateShape(blockstate));
        }
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return (VoxelShape)SHAPES_CACHE.get(state);
    }

    private VoxelShape calculateShape(BlockState state) {
        VoxelShape voxelshape = SHAPE_DOT;

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            int side = (int) state.getValue((Property) PROPERTY_BY_DIRECTION.get(direction));
            if (side == 1) {
                voxelshape = Shapes.or(voxelshape, (VoxelShape) SHAPES_FLOOR.get(direction));
            } else if (side == 2) {
                voxelshape = Shapes.or(voxelshape, (VoxelShape) SHAPES_UP.get(direction));
            }
        }

        return voxelshape;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.getConnectionState(context.getLevel(), this.crossState, context.getClickedPos());
    }

    private BlockState getConnectionState(BlockGetter level, BlockState state, BlockPos pos) {
        boolean flag = isDot(state);
        state = this.getMissingConnections(level, (BlockState)this.defaultBlockState(), pos);
        if (flag && isDot(state)) {
            return state;
        } else {
            boolean flag1 = state.getValue(NORTH) != 0;
            boolean flag2 = state.getValue(SOUTH) != 0;
            boolean flag3 = state.getValue(EAST) != 0;
            boolean flag4 = state.getValue(WEST) != 0;
            boolean flag5 = !flag1 && !flag2;
            boolean flag6 = !flag3 && !flag4;
            if (!flag4 && flag5) {
                state = (BlockState)state.setValue(WEST, 1);
            }

            if (!flag3 && flag5) {
                state = (BlockState)state.setValue(EAST, 1);
            }

            if (!flag1 && flag6) {
                state = (BlockState)state.setValue(NORTH, 1);
            }

            if (!flag2 && flag6) {
                state = (BlockState)state.setValue(SOUTH, 1);
            }

            return state;
        }
    }

    private BlockState getMissingConnections(BlockGetter level, BlockState state, BlockPos pos) {
        boolean flag = !level.getBlockState(pos.above()).isRedstoneConductor(level, pos);
        Iterator var5 = Direction.Plane.HORIZONTAL.iterator();

        while(var5.hasNext()) {
            Direction direction = (Direction)var5.next();
            if (((int)state.getValue((Property)PROPERTY_BY_DIRECTION.get(direction))) == 0) {
                int side = this.getConnectingSide(level, pos, direction, flag);
                state = (BlockState)state.setValue((Property)PROPERTY_BY_DIRECTION.get(direction), side);
            }
        }

        return state;
    }

    private static boolean isDot(BlockState state) {
        return (int) state.getValue(NORTH) == 0 && (int) state.getValue(SOUTH) == 0 && (int)state.getValue(EAST) == 0 && (int)state.getValue(WEST) == 0;
    }

    private int getConnectingSide(BlockGetter level, BlockPos pos, Direction face) {
        return this.getConnectingSide(level, pos, face, !level.getBlockState(pos.above()).isRedstoneConductor(level, pos));
    }

    private int getConnectingSide(BlockGetter level, BlockPos pos, Direction direction, boolean nonNormalCubeAbove) {
        BlockPos blockpos = pos.relative(direction);
        BlockState blockstate = level.getBlockState(blockpos);
        if (nonNormalCubeAbove) {
            boolean flag = blockstate.getBlock() instanceof TrapDoorBlock || this.canSurviveOn(level, blockpos, blockstate);
            if (flag && canConnectTo(level.getBlockState(blockpos.above()))) {
                if (blockstate.isFaceSturdy(level, blockpos, direction.getOpposite())) {
                    return 2;
                }

                return 1;
            }
        }

        if (canConnectTo(level.getBlockState(blockpos))) {
            return 1;
        } else if (canConnectTo(level.getBlockState(blockpos))) {
            return 0;
        } else {
            BlockPos blockPosBelow = blockpos.below();
            return canConnectTo(level.getBlockState(blockPosBelow)) ? 1 : 0;
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return this.canSurviveOn(level, blockpos, blockstate);
    }

    private boolean canSurviveOn(BlockGetter level, BlockPos pos, BlockState state) {
        return state.isFaceSturdy(level, pos, Direction.UP) || state.is(Blocks.HOPPER);
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock()) && !level.isClientSide) {
            Iterator var6 = Direction.Plane.VERTICAL.iterator();

            while(var6.hasNext()) {
                Direction direction = (Direction)var6.next();
                level.updateNeighborsAt(pos.relative(direction), this);
            }
        }
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!movedByPiston && !state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, movedByPiston);
            if (!level.isClientSide) {
                Direction[] var6 = Direction.values();
                int var7 = var6.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    Direction direction = var6[var8];
                    level.updateNeighborsAt(pos.relative(direction), this);
                }
            }
        }
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide) {
            if (!state.canSurvive(level, pos)) {
                dropResources(state, level, pos);
                level.removeBlock(pos, false);
            }
            else {
                level.setBlock(pos, this.getConnectionState(level, this.crossState, pos), 3);
            }
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{NORTH, EAST, SOUTH, WEST});
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random){

    }

    public boolean canConnectTo(BlockState state){
        return state.is(PyroBlocks.GUN_POWDER_WIRE.get());
    }

    static{
        NORTH = IntegerProperty.create("north", 0, 2);
        EAST = IntegerProperty.create("east", 0, 2);
        SOUTH = IntegerProperty.create("south", 0, 2);
        WEST = IntegerProperty.create("west", 0, 2);

        PROPERTY_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));

        SHAPE_DOT = Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
        SHAPES_FLOOR = Maps.newEnumMap(
                ImmutableMap.of(
                        Direction.NORTH,
                        Block.box(3.0, 0.0, 0.0, 13.0, 1.0, 13.0),
                        Direction.SOUTH,
                        Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 16.0),
                        Direction.EAST,
                        Block.box(3.0, 0.0, 3.0, 16.0, 1.0, 13.0),
                        Direction.WEST,
                        Block.box(0.0, 0.0, 3.0, 13.0, 1.0, 13.0)));
        SHAPES_UP = Maps.newEnumMap(
                ImmutableMap.of(
                        Direction.NORTH,
                        Shapes.or(
                                (VoxelShape)SHAPES_FLOOR.get(Direction.NORTH),
                                Block.box(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)
                        ),
                        Direction.SOUTH,
                        Shapes.or((VoxelShape)SHAPES_FLOOR.get(Direction.SOUTH),
                                Block.box(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)
                        ),
                        Direction.EAST,
                        Shapes.or((VoxelShape)SHAPES_FLOOR.get(Direction.EAST),
                                Block.box(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)
                        ),
                        Direction.WEST,
                        Shapes.or((VoxelShape)SHAPES_FLOOR.get(Direction.WEST),
                                Block.box(0.0, 0.0, 3.0, 1.0, 16.0, 13.0)
                        )
                )
        );
        SHAPES_CACHE = Maps.newHashMap();

        COLOR = new Vec3(0.5, 0.5, 0.5);
    }
}