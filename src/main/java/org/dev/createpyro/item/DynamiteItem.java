package org.dev.createpyro.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dev.createpyro.entity.ThrownDynamite;

//TODO: make it spawn the throwndynamite
public class DynamiteItem extends Item {
    public static final ResourceLocation IGNITED = new ResourceLocation("assets/createpyro/models/item/lit_dynamite.json");
    private final int explosionRadius = 5;
    private final float min = 0.45F;
    private final float max = 0.55F;
    private float explosionRandom = (float) ((Math.random() * (max - min)) + min);
    private int timeSinceIgnition;
    //measured in ticks
    public boolean isIgnited(ItemStack dynamite){
        if (dynamite.hasTag()){
            CompoundTag oldTag = dynamite.getTag();
            if (oldTag.contains("ignited")) {
                return true;
            }
        }
        return false;
    }

    public DynamiteItem(Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand, Entity pos, ItemStack dynamite) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (!level.isClientSide && isIgnited(dynamite)) {
            ThrownDynamite thrownDynamite = new ThrownDynamite(level, player);
            thrownDynamite.setItem(itemStack);
            thrownDynamite.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0f, 0.5f, 1.0f);
            level.addFreshEntity(thrownDynamite);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild && isIgnited(dynamite)) {
            itemStack.shrink(1);
        }
        Item offhandItem = player.getOffhandItem().getItem();
        Item handItem = player.getMainHandItem().getItem();
        if(offhandItem instanceof FlintAndSteelItem && handItem instanceof DynamiteItem || offhandItem instanceof DynamiteItem && handItem instanceof FlintAndSteelItem){

        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
    private void onIgnition(Level level, Player player, Entity pos) {
        if(timeSinceIgnition == 100) {
            explosion(level, player, pos);
        }
    }
    private void explosion (Level level, Player player, Entity pos){
        if (!level.isClientSide) {
            level.playSound(player, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            player.level().explode(player, player.getX(), player.getY(), player.getZ(), (float) this.explosionRadius * explosionRandom, Level.ExplosionInteraction.MOB);
        }
    }
    public void ignite(ItemStack stack){
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("ignited", true);
        stack.setTag(tag);
    }
    @Override
    public final int getMaxStackSize(final ItemStack stack) {
        return 16;
    }


    @Override
    public void onInventoryTick(final ItemStack stack, final Level level, final Player player, final int slotIndex, final int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
      /*  if (isIgnited(dynamite)) {
            timeSinceIgnition = timeSinceIgnition +1;}*/
    }
}
