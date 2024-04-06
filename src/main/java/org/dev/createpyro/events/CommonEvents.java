package org.dev.createpyro.events;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dev.createpyro.Pyro;
import org.dev.createpyro.block.GunPowderWireBlock;
import org.dev.createpyro.registry.PyroBlocks;

@Mod.EventBusSubscriber(modid = Pyro.ID)
public class CommonEvents {

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
}
