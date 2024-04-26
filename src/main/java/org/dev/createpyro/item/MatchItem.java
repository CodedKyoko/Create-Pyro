package org.dev.createpyro.item;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Random;

public class MatchItem extends Item {
    private Random random = new Random();
    public MatchItem(Properties properties) {
        super(properties.defaultDurability(200));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack match = player.getItemInHand(usedHand);

        if (match.hasTag()){
            CompoundTag oldTag = match.getTag();
            if (oldTag.contains("ignited")) {
                return InteractionResultHolder.fail(match);
            }
        }

        CompoundTag tag = new CompoundTag();
        tag.putBoolean("ignited", true);
        match.setTag(tag);

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
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

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 64;
    }
}
