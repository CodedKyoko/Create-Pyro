package org.dev.createpyro.events;


import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dev.createpyro.Pyro;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import org.dev.createpyro.particle.SparksParticles;
import org.dev.createpyro.registry.PyroParticles;

@Mod.EventBusSubscriber(modid = Pyro.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PyroEventBusEvents {

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(PyroParticles.SPARK_PARTICLES.get(),
                SparksParticles.Provider::new);
    }
}
