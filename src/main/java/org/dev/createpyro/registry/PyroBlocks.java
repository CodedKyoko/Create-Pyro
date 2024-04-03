package org.dev.createpyro.registry;

import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.item.Items;
import org.dev.createpyro.block.GunPowderWireBlock;

import static org.dev.createpyro.Pyro.PYRO_REGISTRATE;

public class PyroBlocks {
    public static void init() {
    }

    public static final BlockEntry<GunPowderWireBlock> GUN_WIRE = PYRO_REGISTRATE.block("gun_powder_wire", GunPowderWireBlock::new)
            .properties(p -> p.strength(0.01F).noOcclusion())
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), AssetLookup.standardModel(ctx, prov)))
            .loot((lt, b) -> lt.add(b, lt.createSingleItemTable(Items.GUNPOWDER)))
            .addLayer(() -> RenderType::cutoutMipped)
            .register();
}
