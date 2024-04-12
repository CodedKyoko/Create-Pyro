package org.dev.createpyro.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dev.createpyro.Pyro;
import org.dev.createpyro.registry.PyroBlocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = Pyro.ID)
public class CommonEvents {
    public static final Logger LOGGER = LoggerFactory.getLogger(Pyro.NAME);
    private static Random random = new Random();

    @SubscribeEvent
    public static InteractionResult gunPowderRightClick(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() != Items.GUNPOWDER) return InteractionResult.FAIL;

        BlockPos pos = event.getPos();
        BlockState state = PyroBlocks.GUN_POWDER_WIRE.get().getStateForPlacement(
                new BlockPlaceContext(event.getEntity(), event.getHand(), event.getItemStack(),
                        event.getHitVec()));

        Level _level = event.getLevel();

        Entity entity = event.getEntity();

        if (entity instanceof Player _plr ? !_plr.getAbilities().instabuild : true){
            stack.shrink(1);
        }
        if (!_level.isClientSide()) {
            _level.playSound(null, pos, state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1, 1);
        }
        event.getLevel().setBlockAndUpdate(pos.relative(event.getFace()), state);

        return InteractionResult.SUCCESS;
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        Level level = event.getLevel();
        Explosion explosion = event.getExplosion();
        List<BlockPos> blocks = event.getAffectedBlocks();
        Vec3 pos = explosion.getPosition();
        BlockPos blockPos = new BlockPos((int)pos.x, (int)pos.y, (int)pos.z);

        for (BlockPos affectedPos : blocks){
            Vec3 relative = new Vec3(
                    (affectedPos.getX() - pos.x + 0.5) * 0.3F + random.nextDouble(-0.1, 0.1),
                    (affectedPos.getY() - pos.y + 0.5) * 0.3F + random.nextDouble(-0.1, 0.1) + 0.3,
                    (affectedPos.getZ() - pos.z + 0.5) * 0.3F + random.nextDouble(-0.1, 0.1)
                    );
            BlockState state = level.getBlockState(affectedPos);
            FallingBlockEntity entity = fall(
                    level,
                    new Vec3(
                            affectedPos.getX(),
                            affectedPos.getY(),
                            affectedPos.getZ()
                    ),
                    state
            );

            if (entity == null){
                continue;
            }


            entity.setDeltaMovement(
                    relative
            );
            level.setBlock(affectedPos, Blocks.AIR.defaultBlockState(), 3);
            level.addFreshEntity(entity);
        }
    }

    private static FallingBlockEntity fall(Level level, Vec3 pos, BlockState state){
        if (state.is(Blocks.AIR)){
            return null;
        }

        FallingBlockEntity entity = EntityType.FALLING_BLOCK.create(level);
        entity.blocksBuilding = true;
        entity.setPos(new Vec3(
                pos.x() + 0.5,
                pos.y(),
                pos.z() + 0.5
        ));
        entity.setDeltaMovement(Vec3.ZERO);
        entity.xo = pos.x();
        entity.yo = pos.y();
        entity.zo = pos.z();
        entity.setStartPos(entity.blockPosition());
        return entity;
    }
}
