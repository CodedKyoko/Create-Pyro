package org.dev.createpyro;



import com.simibubi.create.foundation.data.CreateRegistrate;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.StringUtils;

import org.dev.createpyro.registry.PyroBlockEntities;
import org.dev.createpyro.registry.PyroBlocks;
import org.dev.createpyro.registry.PyroItems;
import org.dev.createpyro.registry.PyroParticles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Mod(Pyro.MOD_ID)
public class Pyro {
	public static final String MOD_ID = "createpyro";
	public static final String NAME = "Create: Pyro";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	public static final String VERSION = getVersion();
    //public static final CreateRegistrate PYRO_REGISTRATE = CreateRegistrate.create(Pyro.ID);

	public static CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

	public Pyro() {

		LOGGER.info("Create: Pyro v{} initializing!", VERSION);

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext context = ModLoadingContext.get();
		MinecraftForge.EVENT_BUS.register(this);
		IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
		//PYRO_REGISTRATE.registerEventListeners(modEventBus);
		PyroBlocks.register(modEventBus);
		PyroBlockEntities.init();
		PyroItems.register();
		PyroParticles.register(modEventBus);
		REGISTRATE.registerEventListeners(modEventBus);

		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::commonSetup);
	}

	public static String toHumanReadable(String key) {
		String s = key.replaceAll("_", " ");
		s = Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(s)).map(StringUtils::capitalize).collect(Collectors.joining(" "));
		s = StringUtils.normalizeSpace(s);
		return s;
	}

	public static ResourceLocation asResource(String name) {
		return new ResourceLocation(MOD_ID, name);
	}

	private static String getVersion() {
		Optional<? extends ModContainer> container = ModList.get().getModContainerById(MOD_ID);
		if(container.isEmpty()) {
			LOGGER.warn("Could not find mod container for modid " + MOD_ID);
			return "UNKNOWN";
		}
		return container.get()
				.getModInfo()
				.getVersion()
				.toString();
	}
	
	public void commonSetup(final RegisterEvent event) {
	}
	
	private void clientSetup(final FMLClientSetupEvent event){
		ItemBlockRenderTypes.setRenderLayer(PyroBlocks.GUN_POWDER_WIRE.get(), RenderType.cutoutMipped());
	}
}
