package org.dev.createpyro.registry;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.pathfinder.Path;
import org.dev.createpyro.advancements.CreateAdvancements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static org.dev.createpyro.advancements.CreateAdvancements.TaskType.SECRET;
import static org.dev.createpyro.advancements.CreateAdvancements.TaskType.SILENT;


public class PyroAdvancements implements DataProvider {
    public static final List<CreateAdvancements> ENTRIES = new ArrayList<CreateAdvancements>();
    public static final CreateAdvancements START = null,

     ROOT = create("root", b -> b.icon(Items.TNT)
            .title("Welcome to Create Pyro")
            .description("Let there be fire!")
            .awardedForFree()
            .special(SILENT)),
      IGNITE = create("ignite", b -> b.icon(Items.FLINT_AND_STEEL)
            .title("Brighter than candles")
            .description("Ignite an item")
            .after(ROOT)),
      SELF_EXPLODE = create("self_explode", b -> b.icon(Items.TNT)
            .title("Remember to lauch")
            .description("Wait too long to launch dynamite")
            .special(SECRET)
            .after(IGNITE)),
     END = null;
    private static CreateAdvancements create(String id, UnaryOperator<CreateAdvancements.Builder> b) {
        return new CreateAdvancements(id, b);
    }

        // Datagen

    private final PackOutput output;

    public PyroAdvancements(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        PackOutput.PathProvider pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
        List<CompletableFuture<?>> futures = new ArrayList<>();

        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> consumer = (advancement) -> {
            ResourceLocation id = advancement.getId();
            if (!set.add(id))
                throw new IllegalStateException("Duplicate advancement " + id);
            Path path = (Path) pathProvider.json(id);
            futures.add(DataProvider.saveStable(cache, (JsonElement) advancement.deconstruct()
                    .serializeToJson(), (java.nio.file.Path) path));
        };

        for (CreateAdvancements advancement : ENTRIES) {
            try {
                advancement.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Create Pyro's Advancements";
    }

    public static void provideLang(BiConsumer<String, String> consumer) {
        for (CreateAdvancements advancement : ENTRIES)
            advancement.toString();
    }

    public static void register() {}
}
