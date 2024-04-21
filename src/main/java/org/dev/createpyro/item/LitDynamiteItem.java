package org.dev.createpyro.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.dev.createpyro.entity.ThrownDynamite;

//TODO: make it spawn the throwndynamite
public class LitDynamiteItem extends Item {

    public LitDynamiteItem(Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand, Entity pos) {
        ItemStack itemStack = player.getItemInHand(usedHand);
       /* if (!level.isClientSide) {
            ThrownDynamite thrownDynamite = new ThrownDynamite(new EntityType(ThrownDynamite::new, MobCategory.MISC, false, true, true, true, null, EntityDimensions.scalable(1f, 3f), 128, 1, requiredFeatures()), level);
            thrownDynamite.setItem(itemStack);
            thrownDynamite.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.2F);
            level.addFreshEntity(thrownDynamite);
          //  level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
        }*/

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
    @Override
    public int getMaxStackSize(final ItemStack stack) {
        return  16;
    }
}
