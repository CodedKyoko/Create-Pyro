package org.dev.createpyro.entity;


import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.dev.createpyro.registry.PyroItems;

//TODO:make it not crash :(
public class ThrownDynamite extends ThrowableItemProjectile{

    private final int explosionRadius = 5;
    private final float min = 0.2F;
    private final float max = 0.7F;
    private float explosionRandom = (float) ((Math.random() * (max - min)) + min);
    //silly little random factor :3
    private Float timeSinceLaunch = 0F;


    private static EntityType<? extends net.minecraft.world.entity.projectile.ThrowableItemProjectile> ThrowableItemProjectile;
    private static final EntityType<? extends ThrowableItemProjectile> entityType = ThrowableItemProjectile;




    public ThrownDynamite(Level level, LivingEntity shooter) {
        super((EntityType<? extends ThrowableItemProjectile>)EntityType.POTION, shooter, level);
    }

    public ThrownDynamite(EntityType<Entity> entityEntityType, Level level) {
        super((EntityType<? extends ThrowableItemProjectile>)entityType, level);
    }




    private void explode(Level level, Player player, Entity pos) {
        if (!this.level().isClientSide && timeSinceLaunch == 100F) {
            level.playSound(player, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) this.explosionRadius * explosionRandom, Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }
    @Override
    protected Item getDefaultItem() {
        return PyroItems.DYNAMITE.asItem();
    }

    @Override
    public void tick() {
        timeSinceLaunch = timeSinceLaunch+1f;
        super.tick();
    }
}
