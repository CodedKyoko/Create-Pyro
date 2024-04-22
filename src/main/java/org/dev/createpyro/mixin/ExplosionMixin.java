package org.dev.createpyro.mixin;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.units.qual.A;
import org.dev.createpyro.mixin_accessor.ItemDropFallingTracker;
import org.dev.createpyro.mixin_accessor.WidenedFallingBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    //TODO: clean up
    @Shadow @Final private Level level;
    @Shadow public abstract Vec3 getPosition();
    @Shadow @Final private Vec3 position;
    @Inject(method = "finalizeExplosion", at = @At(value = "HEAD"))
    public void finalizeExplosion_HEAD(boolean spawnParticles, CallbackInfo ci) {
        System.out.println("aFINISHED");
        ItemDropFallingTracker.HAS_GENERATED_SPAWNMAP = false;
        ItemDropFallingTracker.LEVEL =level;
        //        ItemDropFallingTracker.CURRENT_THREAD_EXPLOSION = (Explosion) (Object) this;
    }
    
    private static HashMap<BlockPos, BlockState> brokenStates = new HashMap<>();
    
    @Redirect(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;spawnAfterBreak(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Z)V"))
    public void spawnAfterBreak(BlockState instance, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropExperience) {
//        ItemDropFallingTracker.CURRENT_THREAD_BLOCKSTATE = instance;
//        ItemDropFallingTracker.CURRENT_THREAD_BLOCKPOS = pos;
        brokenStates.put(pos, instance);
        instance.spawnAfterBreak(level, pos, stack, dropExperience);
    }
    
    private static ObjectArrayList<Pair<ItemStack, BlockPos>> stored = null;
    @ModifyVariable(method = "finalizeExplosion", at = @At(value = "STORE", ordinal = 0))
    public ObjectArrayList<Pair<ItemStack, BlockPos>> store_ObjectArrayList(ObjectArrayList<Pair<ItemStack, BlockPos>> arrayList) {
        stored = arrayList;
        return arrayList;
    }
    //Tracking if there is multiple items in a block
    HashMap<BlockPos, List<ItemStack>> spawnMap = new HashMap<>();
    
    @Inject(method = "finalizeExplosion", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/level/block/Block;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private void popResource(CallbackInfo callbackInfo) {
        if (!ItemDropFallingTracker.HAS_GENERATED_SPAWNMAP) {
            spawnMap = new HashMap<>();
            for (Pair<ItemStack, BlockPos> posPair : stored) {
                if (!spawnMap.containsKey(posPair.getSecond()))
                    spawnMap.put(posPair.getSecond(), new ArrayList<>());
                spawnMap.get(posPair.getSecond()).add(posPair.getFirst());
            }
            
            for (Map.Entry<BlockPos, List<ItemStack>> entry : spawnMap.entrySet()) {
                
                if (entry.getValue().size() != 1) {
                    for (ItemStack stack : entry.getValue())
                        Block.popResource(level, BlockPos.containing(position), stack);
                    return;
                }
                BlockPos blockPos = entry.getKey();
                Item dropItem = entry.getValue().get(0).getItem();
                if (!(dropItem instanceof BlockItem dropBlockItem)){
                    for (ItemStack stack : entry.getValue())
                        Block.popResource(level, BlockPos.containing(position), stack);
                    return;
                }
                System.out.println("IS_REPLACING");
                System.out.println("ItemEntity");
                System.out.println("BlockItem");
                BlockState blockstateToDrop = brokenStates.get(blockPos);
                if (!blockstateToDrop.getBlock().equals(dropBlockItem.getBlock()))
                    blockstateToDrop = dropBlockItem.getBlock().defaultBlockState();
                
                System.out.println("SPAWNING");
                
                Vec3 sourcePos = getPosition();
                
                Vec3 relative = new Vec3(
                    (blockPos.getX() - sourcePos.x + 0.5) * 0.5F,
                    (blockPos.getY() - sourcePos.y + 0.5) * 0.5F + 0.5,
                    (blockPos.getZ() - sourcePos.z + 0.5) * 0.5F
                );
                
                FallingBlockEntity fallingblockentity = new FallingBlockEntity(EntityType.FALLING_BLOCK, ItemDropFallingTracker.LEVEL);
                ((WidenedFallingBlockEntity) fallingblockentity).initWithVelocity(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockstateToDrop, relative);
                ItemDropFallingTracker.LEVEL.addFreshEntity(fallingblockentity);
                System.out.println("SPAWNed");
            }
            ItemDropFallingTracker.HAS_GENERATED_SPAWNMAP = true;
        }
    }
    
    @Redirect(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    public void popResource(Level level, BlockPos pos, ItemStack stack) {}
    
    @Inject(method = "finalizeExplosion", at = @At(value = "TAIL"))
    public void finalizeExplosion_TAIL(boolean spawnParticles, CallbackInfo ci) {
        ItemDropFallingTracker.HAS_GENERATED_SPAWNMAP = false;
        System.out.println("FINISHED");
    }
    
}
