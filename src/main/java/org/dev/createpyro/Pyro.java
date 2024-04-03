package org.dev.createpyro;



import com.simibubi.create.foundation.data.CreateRegistrate;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.commons.lang3.StringUtils;

import org.dev.createpyro.registry.PyroBlockEntities;
import org.dev.createpyro.registry.PyroBlocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Mod(Pyro.ID)
public class Pyro {
	public static final String ID = "createpyro";
	public static final String NAME = "Create: Pyro";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	public static final String VERSION = getVersion();
    public static final CreateRegistrate PYRO_REGISTRATE = CreateRegistrate.create(Pyro.ID);


	public Pyro() {

		LOGGER.info("Create: Pyro v{} initializing!", VERSION);

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext context = ModLoadingContext.get();
		MinecraftForge.EVENT_BUS.register(this);
		IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
		PYRO_REGISTRATE.registerEventListeners(modEventBus);
		PyroBlocks.init();
		PyroBlockEntities.init();

	}

	public static String toHumanReadable(String key) {
		String s = key.replaceAll("_", " ");
		s = Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(s)).map(StringUtils::capitalize).collect(Collectors.joining(" "));
		s = StringUtils.normalizeSpace(s);
		return s;
	}

	public static ResourceLocation asResource(String name) {
		return new ResourceLocation(ID, name);
	}

	private static String getVersion() {
		Optional<? extends ModContainer> container = ModList.get().getModContainerById(ID);
		if(container.isEmpty()) {
			LOGGER.warn("Could not find mod container for modid " + ID);
			return "UNKNOWN";
		}
		return container.get()
				.getModInfo()
				.getVersion()
				.toString();
	}
}
