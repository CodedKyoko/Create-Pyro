package org.dev.createpyro.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dev.createpyro.Pyro;
import org.dev.createpyro.registry.PyroBlocks;
import org.dev.createpyro.registry.PyroParticles;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GunPowderWireBlock extends Block {
    public static final IntegerProperty NORTH;
    public static final IntegerProperty EAST;
    public static final IntegerProperty SOUTH;
    public static final IntegerProperty WEST;
    public static final IntegerProperty IGNITION_TIME;
    public static final Map<Direction, IntegerProperty> PROPERTY_BY_DIRECTION;
    private static final VoxelShape SHAPE_DOT;
    private static final Map<Direction, VoxelShape> SHAPES_FLOOR;
    private static final Map<Direction, VoxelShape> SHAPES_UP;
    private static final Map<BlockState, VoxelShape> SHAPES_CACHE;
    private static final Vec3 COLOR;
    private final BlockState crossState;
    public static final Logger LOGGER = LoggerFactory.getLogger(Pyro.NAME);


    public GunPowderWireBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                (BlockState) ((BlockState) ((BlockState) ((BlockState) ((BlockState) ((BlockState) this.stateDefinition.any())
                        .setValue(NORTH, 0))
                        .setValue(EAST, 0))
                        .setValue(SOUTH, 0))
                        .setValue(WEST, 0))
                        .setValue(IGNITION_TIME, 0)
        );
        this.crossState = (BlockState)(
                (BlockState) ((BlockState) ((BlockState) this.defaultBlockState()
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
        state = this.getMissingConnections(level, (BlockState)this.defaultBlockState().setValue(IGNITION_TIME, (Integer)state.getValue(IGNITION_TIME)), pos);
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
            if (flag && canConnectTo(level.getBlockState(blockpos.above()), false)) {
                if (blockstate.isFaceSturdy(level, blockpos, direction.getOpposite())) {
                    return 2;
                }

                return 1;
            }
        }

        if (canConnectTo(level.getBlockState(blockpos), false)) {
            return 1;
        } else if (canConnectTo(level.getBlockState(blockpos), false)) {
            return 0;
        } else {
            BlockPos blockPosBelow = blockpos.below();
            return canConnectTo(level.getBlockState(blockPosBelow), true) ? 1 : 0;
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return this.canSurviveOn(level, blockpos, blockstate);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        boolean ignited = false;
        ItemStack stack = player.getItemInHand(hand);

        /*if (level.isClientSide()){
            return InteractionResult.FAIL;
        }*/
        if (stack.is(Items.FLINT_AND_STEEL)){
            ignited = ignite(level, pos, state);

            if (ignited){
                level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);

                if (player instanceof ServerPlayer) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, pos, stack);
                    stack.hurtAndBreak(1, player, (p) -> {
                        p.broadcastBreakEvent(hand);
                    });
                }
            }
        }
        return ignited ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    public boolean ignite(Level level, BlockPos pos, BlockState state){
        if (state.getValue(IGNITION_TIME) == 0){
            level.setBlock(pos, state.setValue(IGNITION_TIME, 1), 11);

            return true;
        }

        return false;
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
        level.scheduleTick(pos, this, 10);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);

        int i = state.getValue(IGNITION_TIME);
        if (i != 0){
            state = state.setValue(IGNITION_TIME, i+1);
            if (i+1 >= 4){
                igniteNeighbors(level, pos, state);
                level.removeBlock(pos, false);
                level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            }
            else {
                level.setBlock(pos, state, 3);
            }
        }

        level.scheduleTick(pos, this, 10);
    }

    public void igniteNeighbors(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.getValue(IGNITION_TIME) == 0){
            return;
        }

        Iterator iterator = Direction.Plane.HORIZONTAL.iterator();

        while(iterator.hasNext()) {
            Direction direction = (Direction)iterator.next();

            int side = (int)state.getValue((Property)PROPERTY_BY_DIRECTION.get(direction));

            BlockPos blockPos = null;
            LOGGER.info(String.valueOf(side) + " is on side " + direction);
            if (side != 0) {
                if (side == 1) {
                    blockPos = pos.relative(direction);
                    if (!canConnectTo(level.getBlockState(blockPos), false)) {
                        blockPos = blockPos.below();
                    }
                } else if (side == 2) {
                    blockPos = pos.relative(direction).above();
                }
            }
            else if (canConnectTo(level.getBlockState(pos.relative(direction).below()), false)){
                blockPos = pos.relative(direction).below();
            }

            if (blockPos == null){
                continue;
            }
            BlockState blockState = level.getBlockState(blockPos);

            if (blockState.is(PyroBlocks.GUN_POWDER_WIRE.get())){
                this.ignite(level, blockPos, blockState);
            } else if (blockState.getBlock() instanceof TntBlock tnt) {
                tnt.onCaughtFire(blockState, level, blockPos, null, null);
                level.removeBlock(blockPos, false);
            }
        }

        if (canConnectTo(level.getBlockState(pos.below()), false)){
            BlockState blockState = level.getBlockState(pos.below());
            if (blockState.is(PyroBlocks.GUN_POWDER_WIRE.get())){
                this.ignite(level, pos.below(), blockState);
            } else if (blockState.getBlock() instanceof TntBlock tnt) {
                tnt.onCaughtFire(blockState, level, pos.below(), null, null);
                level.removeBlock(pos, false);
                level.removeBlock(pos.below(), false);
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

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(Items.GUNPOWDER);
    }


    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide) {
            if (!state.canSurvive(level, pos)) {
                dropResources(state, level, pos);
                level.removeBlock(pos, false);
            }
            else {
                level.setBlock(pos, this.getConnectionState(level, this.crossState.setValue(IGNITION_TIME, state.getValue(IGNITION_TIME)), pos), 3);
            }
        }
    }

    /*private BlockState igniteIfNeeded(Level level, BlockPos pos, BlockState state){
        if (state.getValue(IGNITION_TIME) != 0){
            return state;
        }

        boolean ignite = false;

        Iterator iterator = Direction.Plane.HORIZONTAL.iterator();

        while(iterator.hasNext()) {
            Direction direction = (Direction)iterator.next();
            BlockPos blockpos = pos.relative(direction);

            BlockState blockstate = level.getBlockState(blockpos);
            if (!level.getBlockState(pos.above()).isRedstoneConductor(level, pos)) {
                boolean flag = blockstate.getBlock() instanceof TrapDoorBlock || this.canSurviveOn(level, blockpos, blockstate);
                if (flag && canConnectTo(level.getBlockState(blockpos.above())) && level.getBlockState(blockpos.above()).getValue(IGNITION_TIME) != 0) {
                    ignite = true;
                }
            }

            if (canConnectTo(level.getBlockState(blockpos)) && blockstate.getValue(IGNITION_TIME) != 0) {
                ignite = true;
            } else {
                BlockPos blockPosBelow = blockpos.below();
                if (canConnectTo(level.getBlockState(blockPosBelow)) && level.getBlockState(blockPosBelow).getValue(IGNITION_TIME) != 0){
                    ignite = true;
                }
            }
        }

        return state.setValue(IGNITION_TIME, ignite ? 1 : 0);
    }*/

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{NORTH, EAST, SOUTH, WEST, IGNITION_TIME});
    }

    public boolean canConnectTo(BlockState state, boolean isDown){
        return state.is(PyroBlocks.GUN_POWDER_WIRE.get()) || (state.is(Blocks.TNT) && !isDown) || (state.is(PyroBlocks.RED_BUTTON.get()));
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int i = (Integer)state.getValue(IGNITION_TIME);
        if (i != 0) {
            Iterator var6 = Direction.Plane.HORIZONTAL.iterator();

            while(var6.hasNext()) {
                Direction direction = (Direction)var6.next();
                int side = (int)state.getValue((Property)PROPERTY_BY_DIRECTION.get(direction));
                switch (side) {
                    case 2:
                        this.spawnParticlesAlongLine(level, random, pos, direction, Direction.UP, -0.5F, 0.5F);
                    case 1:
                        this.spawnParticlesAlongLine(level, random, pos, Direction.DOWN, direction, 0.0F, 0.5F);
                        break;
                    case 0:
                    default:
                        this.spawnParticlesAlongLine(level, random, pos, Direction.DOWN, direction, 0.0F, 0.3F);
                }
            }
            if (random.nextInt(2) == 0) {
                double d0 = (double)pos.getX() + random.nextDouble();
                double d1 = (double)pos.getY() + 0.0;
                double d2 = (double)pos.getZ() + random.nextDouble();
                level.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0, 0.0, 0.0);
                level.playLocalSound(d0, d1, d2, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
            double d0 = (double)pos.getX() + random.nextDouble();
            double d1 = (double)pos.getY() + 0.0;
            double d2 = (double)pos.getZ() + random.nextDouble();
            level.addParticle(PyroParticles.SPARK_PARTICLES.get(), d0, d1, d2, 0.0, 0.0, 0.0);
            d0 = (double)pos.getX() + random.nextDouble();
            d1 = (double)pos.getY() + 0.0;
            d2 = (double)pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0, 0.0, 0.0);
        }

    }

    private void spawnParticlesAlongLine(Level level, RandomSource random, BlockPos pos, Direction xDirection, Direction zDirection, float min, float max) {
        float f = max - min;
        if (true) {
            float f1 = 0.4375F;
            float f2 = min + f * random.nextFloat();
            double d0 = 0.5 + (double)(0.4375F * (float)xDirection.getStepX()) + (double)(f2 * (float)zDirection.getStepX());
            double d1 = 0.5 + (double)(0.4375F * (float)xDirection.getStepY()) + (double)(f2 * (float)zDirection.getStepY());
            double d2 = 0.5 + (double)(0.4375F * (float)xDirection.getStepZ()) + (double)(f2 * (float)zDirection.getStepZ());
            //level.addParticle(new DustParticleOptions(new Vector3f(0.1F, 0.1F, 0.1F), 1.0F), (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, 0.0, 0.0, 0.0);
            level.addParticle(PyroParticles.SPARK_PARTICLES.get(), d0, d1, d2, 0.0, 0.0, 0.0);
            level.playLocalSound(d0, d1, d2, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);

        }

    }


    static{
        NORTH = IntegerProperty.create("north", 0, 2);
        EAST = IntegerProperty.create("east", 0, 2);
        SOUTH = IntegerProperty.create("south", 0, 2);
        WEST = IntegerProperty.create("west", 0, 2);

        IGNITION_TIME = IntegerProperty.create("ignition_time", 0, 4);

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