package org.dev.createpyro.registry;

import com.tterrag.registrate.util.entry.EntityEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import org.dev.createpyro.entity.ThrownDynamite;

import static org.dev.createpyro.Pyro.REGISTRATE;

public class PyroEntities {
    public static void register() {
    }

    public static EntityEntry<Entity> THROWN_DYNAMITE =
            REGISTRATE.entity("thrown_dynamite", ThrownDynamite::new, MobCategory.MISC)
                    .register();
}
