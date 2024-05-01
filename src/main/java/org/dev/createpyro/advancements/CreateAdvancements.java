package org.dev.createpyro.advancements;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.advancement.SimpleCreateTrigger;
import com.simibubi.create.foundation.utility.Components;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.dev.createpyro.Pyro;
import org.dev.createpyro.registry.PyroAdvancements;
import org.dev.createpyro.registry.PyroTriggers;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class CreateAdvancements {
    static final ResourceLocation BACKGROUND = Create.asResource("textures/gui/advancements.png");
    private static final String LANG = "advancement." + Pyro.MOD_ID + ".";
    private static final String SECRET_SUFFIX = "\n\u00A77(Hidden Advancement)";
    private final Advancement.Builder builder;
    private String title;
    private String description;
    private String id;
    private CreateAdvancements parent;
    private SimpleCreateTrigger builtinTrigger;
    Advancement datagenResult;


    public CreateAdvancements(String id, UnaryOperator<Builder> b) {
        this.builder = Advancement.Builder.advancement();
        this.id = id;

        Builder t = new Builder();
        b.apply(t);

        if (!t.externalTrigger) {
            builtinTrigger = PyroTriggers.addSimple(id + "_builtin");
            builder.addCriterion("0", builtinTrigger.instance());
        }

        builder.display(t.icon, Components.translatable(titleKey()),
                Components.translatable(descriptionKey()).withStyle(s -> s.withColor(0xDBA213)),
                id.equals("root") ? BACKGROUND : null, t.type.frame, t.type.toast, t.type.announce, t.type.hide);

        if (t.type == TaskType.SECRET)
            description += SECRET_SUFFIX;

        PyroAdvancements.ENTRIES.add(this);
    }

    private String titleKey() {
        return LANG + id;
    }

    private String descriptionKey() {
        return titleKey() + ".desc";
    }

    public boolean isAlreadyAwardedTo(Player player) {
        if (!(player instanceof ServerPlayer sp))
            return true;
        Advancement advancement = sp.getServer()
                .getAdvancements()
                .getAdvancement(Create.asResource(id));
        if (advancement == null)
            return true;
        return sp.getAdvancements()
                .getOrStartProgress(advancement)
                .isDone();
    }

    public void awardTo(Player player) {
        if (!(player instanceof ServerPlayer sp))
            return;
        if (builtinTrigger == null)
            throw new UnsupportedOperationException(
                    "Advancement " + id + " uses external Triggers, it cannot be awarded directly");
        builtinTrigger.trigger(sp);
    }

    void save(Consumer<Advancement> t) {
        if (parent != null)
            builder.parent(parent.datagenResult);
        datagenResult = builder.save(t, Create.asResource(id)
                .toString());
    }

    void provideLang(BiConsumer<String, String> consumer) {
        consumer.accept(titleKey(), title);
        consumer.accept(descriptionKey(), description);
    }

    public static enum TaskType {


        SILENT(FrameType.TASK, false, false, false),
        NORMAL(FrameType.TASK, true, false, false),
        NOISY(FrameType.TASK, true, true, false),
        EXPERT(FrameType.GOAL, true, true, false),
        SECRET(FrameType.GOAL, true, true, true),

        ;

        private FrameType frame;
        private boolean toast;
        private boolean announce;
        private boolean hide;

        TaskType(FrameType frame, boolean toast, boolean announce, boolean hide) {
            this.frame = frame;
            this.toast = toast;
            this.announce = announce;
            this.hide = hide;
        }
    }
    public class Builder {

        private CreateAdvancements.TaskType type = CreateAdvancements.TaskType.NORMAL;
        private boolean externalTrigger;
        private int keyIndex;
        private ItemStack icon;

        public CreateAdvancements.Builder special(CreateAdvancements.TaskType type) {
            this.type = type;
            return this;
        }

        public CreateAdvancements.Builder after(CreateAdvancements other) {
            CreateAdvancements.this.parent = other;
            return this;
        }

        public CreateAdvancements.Builder icon(ItemProviderEntry<?> item) {
            return icon(item.asStack());
        }

        public CreateAdvancements.Builder icon(ItemLike item) {
            return icon(new ItemStack(item));
        }

        CreateAdvancements.Builder icon(ItemStack stack) {
            icon = stack;
            return this;
        }

        public CreateAdvancements.Builder title(String title) {
            CreateAdvancements.this.title = title;
            return this;
        }

        public CreateAdvancements.Builder description(String description) {
            CreateAdvancements.this.description = description;
            return this;
        }

        CreateAdvancements.Builder whenBlockPlaced(Block block) {
            return externalTrigger(ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(block));
        }

        CreateAdvancements.Builder whenIconCollected() {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(icon.getItem()));
        }

        CreateAdvancements.Builder whenItemCollected(ItemProviderEntry<?> item) {
            return whenItemCollected(item.asStack()
                    .getItem());
        }

        CreateAdvancements.Builder whenItemCollected(ItemLike itemProvider) {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(itemProvider));
        }

        CreateAdvancements.Builder whenItemCollected(TagKey<Item> tag) {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance
                    .hasItems(new ItemPredicate(tag, null, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY,
                            EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, null, NbtPredicate.ANY)));
        }

        public CreateAdvancements.Builder awardedForFree() {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[] {}));
        }

        CreateAdvancements.Builder externalTrigger(CriterionTriggerInstance trigger) {
            builder.addCriterion(String.valueOf(keyIndex), trigger);
            externalTrigger = true;
            keyIndex++;
            return this;
        }

    }


}
