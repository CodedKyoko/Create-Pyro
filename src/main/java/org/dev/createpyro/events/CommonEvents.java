package org.dev.createpyro.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dev.createpyro.Pyro;
import org.dev.createpyro.registry.PyroBlocks;

@Mod.EventBusSubscriber(modid = Pyro.ID)
public class CommonEvents {

    @SubscribeEvent
    public static void gunPowderRightClick(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != Items.GUNPOWDER) return;
        BlockPos pos = event.getPos();
        event.getLevel().setBlockAndUpdate(pos.above(), PyroBlocks.GUN_WIRE.get().defaultBlockState());
    }
}
