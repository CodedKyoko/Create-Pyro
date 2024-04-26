package org.dev.createpyro.item;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.dev.createpyro.block.GunPowderWireBlock;
import org.dev.createpyro.registry.PyroBlocks;

import java.util.Random;

public class MatchItem extends Item {
    private Random random = new Random();
    public MatchItem(Properties properties) {
        super(properties.defaultDurability(200));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack match = player.getItemInHand(usedHand);

        if (isIgnited(match)){
            return InteractionResultHolder.fail(match);
        }

        ignite(match);

        return InteractionResultHolder.sidedSuccess(match, level.isClientSide());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (!isIgnited(stack)){
            ignite(stack);
        }
        else {
            Level level = context.getLevel();
            BlockState block = level.getBlockState(context.getClickedPos());
            if (block.is(PyroBlocks.GUN_POWDER_WIRE.get())){
                GunPowderWireBlock.ignite(level, context.getClickedPos(), block);
            }
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(isIgnited(stack)) {
            if (entity instanceof Player player){
                stack.setDamageValue(stack.getDamageValue() + 1);
                if (stack.getDamageValue() >= stack.getMaxDamage()){
                    stack.shrink(1);
                    level.playSound(player, new BlockPos(player.getBlockX(), player.getBlockY(), player.getBlockZ()), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1F, 1F);
                    stack.setTag(null);
                }
            }
        }
    }

    public boolean isIgnited(ItemStack match){
        if (match.hasTag()){
            CompoundTag oldTag = match.getTag();
            if (oldTag.contains("ignited")) {
                return true;
            }
        }
        return false;
    }

    public void ignite(ItemStack stack){
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("ignited", true);
        stack.setTag(tag);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 64;
    }
}
