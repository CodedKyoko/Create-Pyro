package org.dev.createpyro.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dev.createpyro.Pyro;
import org.dev.createpyro.registry.PyroBlocks;
import org.dev.createpyro.registry.PyroItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mod.EventBusSubscriber(modid = Pyro.MOD_ID)
public class CommonEvents {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(Pyro.NAME);
    
    @SubscribeEvent
    public static InteractionResult gunPowderRightClick(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() != Items.GUNPOWDER) return InteractionResult.PASS;
        
        InteractionResult interactionResult = PyroBlocks.GUN_POWDER_WIRE
            .asItem().useOn(new UseOnContext(event.getEntity(), event.getHand(), event.getHitVec()));
        
        if (event.getLevel().isClientSide && interactionResult.shouldSwing())
            event.getEntity().swing(event.getHand());
        
       return interactionResult;
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        //event.setCanceled(true);
        Level level = event.getLevel();
        Explosion explosion = event.getExplosion();
        List<BlockPos> blocks = event.getAffectedBlocks();
        Vec3 pos = explosion.getPosition();
        BlockPos blockPos = new BlockPos((int)pos.x, (int)pos.y, (int)pos.z);

        for (BlockPos affectedPos : blocks){
            Vec3 relative = new Vec3(
                    (affectedPos.getX() - pos.x) * 0.5F,
                    (affectedPos.getY() - pos.y) * 0.5F + 1,
                    (affectedPos.getZ() - pos.z) * 0.5F
                    );


            FallingBlockEntity entity = FallingBlockEntity.fall(level, affectedPos, level.getBlockState(affectedPos));
            entity.setDeltaMovement(
                    relative
            );
        }


        level.setBlock(blockPos, Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
    }
}
