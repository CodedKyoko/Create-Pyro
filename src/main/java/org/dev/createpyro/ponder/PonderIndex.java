package org.dev.createpyro.ponder;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import org.dev.createpyro.Pyro;
import org.dev.createpyro.ponder.content.GunpowderScenes;
import org.dev.createpyro.registry.PyroBlocks;

public class PonderIndex {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper((Pyro.MOD_ID));

    public PonderIndex() {

    }

    public static void register() {
        HELPER.forComponents(new ItemProviderEntry[]{PyroBlocks.RED_BUTTON}).addStoryBoard("gun_powder/red_button", GunpowderScenes::redButtonStoryBoard);
    }
}
