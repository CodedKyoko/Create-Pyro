package org.dev.createpyro.registry;

import com.simibubi.create.foundation.advancement.CriterionTriggerBase;
import com.simibubi.create.foundation.advancement.SimpleCreateTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import org.dev.createpyro.advancements.CreateAdvancements;

import java.util.LinkedList;
import java.util.List;
import java.util.function.UnaryOperator;

public class PyroTriggers {

    private static final List<CriterionTriggerBase<?>> triggers = new LinkedList<>();

    public PyroTriggers(String id, UnaryOperator<CreateAdvancements.Builder> b) {
    }

    public static SimpleCreateTrigger addSimple(String id) {
        return add(new SimpleCreateTrigger(id));
    }

    private static <T extends CriterionTriggerBase<?>> T add(T instance) {
        triggers.add(instance);
        return instance;
    }
    public static void register() {
        triggers.forEach(CriteriaTriggers::register);
    }
}
