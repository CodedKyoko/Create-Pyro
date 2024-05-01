package org.dev.createpyro.registry;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import org.dev.createpyro.item.DynamiteItem;

import static org.dev.createpyro.Pyro.REGISTRATE;

public class PyroItems {
    
    public static void register() {}

    public static ItemEntry<DynamiteItem> DYNAMITE =
            REGISTRATE.item("dynamite", DynamiteItem::new)
                    .register();


    public static ItemEntry<Item> CRUSHED_COAL =
            REGISTRATE.item("crushed_coal", Item::new).register();

    public static ItemEntry<Item> CLEAN_COAL_DUST =
            REGISTRATE.item("clean_coal_dust", Item::new).register();
}
