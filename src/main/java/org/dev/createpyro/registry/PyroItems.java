package org.dev.createpyro.registry;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import org.dev.createpyro.item.LitDynamiteItem;

import static org.dev.createpyro.Pyro.REGISTRATE;

public class PyroItems {
    
    public static void register() {}

    public static ItemEntry<Item> UNLIT_DYNAMITE =
            REGISTRATE.item("unlit_dynamite", Item::new)
                    .register();

    public static ItemEntry<LitDynamiteItem> LIT_DYNAMITE =
            REGISTRATE.item("lit_dynamite", LitDynamiteItem::new)
                    .removeTab(CreativeModeTabs.SEARCH)
                    .register();
}
