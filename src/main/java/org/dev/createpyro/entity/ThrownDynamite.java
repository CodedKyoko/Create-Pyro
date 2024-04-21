package org.dev.createpyro.entity;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

//TODO:make it not crash :(
public class ThrownDynamite extends ThrowableItemProjectile {
    private final int explosionRadius = 5;
    private final float min = 0.2F;
    private final float max = 0.7F;
    private float explosionRandom = (float) ((Math.random() * (max - min)) + min);
    //silly little random factor :3
    private Float timeSinceLaunch = 0F;


    private static EntityType<? extends net.minecraft.world.entity.projectile.ThrowableItemProjectile> ThrowableItemProjectile;
    private static final EntityType<? extends ThrowableItemProjectile> entityType = ThrowableItemProjectile;


    public ThrownDynamite(EntityType type, Level level) {
        super(type, level);
    }




    @Override
    protected Item getDefaultItem() {
        return null;
    }

    private void explode() {
        if (!this.level().isClientSide && timeSinceLaunch == 5.0F) {

            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) this.explosionRadius * explosionRandom, Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }
}
