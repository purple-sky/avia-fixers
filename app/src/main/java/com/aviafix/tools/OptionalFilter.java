package com.aviafix.tools;

import org.jooq.Condition;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class OptionalFilter {
    private final List<Condition> conditions = new ArrayList();

    public static OptionalFilter build() {
        return new OptionalFilter();
    }

    public OptionalFilter add(Optional<Condition> condition) {
        condition.ifPresent(this.conditions::add);
        return this;
    }

    public Condition combineWithAnd() {
        return this.conditions.stream().reduce(DSL.trueCondition(), Condition::and);
    }

    public Condition combineWithOr() {
        return this.conditions.stream().reduce(DSL.falseCondition(), Condition::or);
    }

    private OptionalFilter() {
    }
}
