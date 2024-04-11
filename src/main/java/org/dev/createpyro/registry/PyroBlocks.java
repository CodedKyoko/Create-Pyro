package org.dev.createpyro.registry;

import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dev.createpyro.Pyro;
import org.dev.createpyro.block.GunPowderWireBlock;

import java.util.function.Supplier;

import static org.dev.createpyro.Pyro.REGISTRATE;

public class PyroBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Pyro.MOD_ID);

    public static final BlockEntry<GunPowderWireBlock> GUN_POWDER_WIRE = REGISTRATE.block("gun_powder_wire",
            (properties) -> new GunPowderWireBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .pushReaction(PushReaction.DESTROY)
            ))
        .loot((registrateBlockLootTables, gunPowderWireBlock) ->
            registrateBlockLootTables.dropOther(gunPowderWireBlock, Items.GUNPOWDER.asItem())
        )
        .simpleItem()
        .register();

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        return toReturn;
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
    public static void init() {
    }
}
