package org.dev.createpyro.registry;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.dev.createpyro.item.LitDynamiteItem;
import org.dev.createpyro.item.MatchItem;

import static org.dev.createpyro.Pyro.REGISTRATE;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PyroItems {
    
    public static void register() {}

    public static ItemEntry<Item> UNLIT_DYNAMITE =
            REGISTRATE.item("unlit_dynamite", Item::new)
                    .register();

    public static ItemEntry<LitDynamiteItem> LIT_DYNAMITE =
            REGISTRATE.item("lit_dynamite", LitDynamiteItem::new)
                    .removeTab(CreativeModeTabs.SEARCH)
                    .register();

    public static ItemEntry<Item> CRUSHED_COAL =
            REGISTRATE.item("crushed_coal", Item::new).register();

    public static ItemEntry<Item> CLEAN_COAL_DUST =
            REGISTRATE.item("clean_coal_dust", Item::new).register();

    public static ItemEntry<MatchItem> MATCH =
            REGISTRATE.item("match", MatchItem::new)
                    .tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
                    .register();

    @SubscribeEvent
    public static void clientLoad(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(MATCH.get(), new ResourceLocation("createpyro:match_ignited"), (itemStackToRender, clientWorld, entity, itemEntityId) -> {
            if (itemStackToRender.getItem() instanceof MatchItem match){
                return (match.isIgnited(itemStackToRender) ? 1 : 0);
            }
            return 0;
        }));
    }
}
