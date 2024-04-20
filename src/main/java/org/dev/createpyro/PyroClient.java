package org.dev.createpyro;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.dev.createpyro.ponder.PonderIndex;

public class PyroClient {
    public static void clientInit(FMLClientSetupEvent event) {
        PonderIndex.register();
    }

    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(PyroClient::clientInit);
    }
}
