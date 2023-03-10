/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.advancement;

import java.util.Collection;

public interface CriterionMerger {
    public static final CriterionMerger AND = collection -> {
        String[][] strings = new String[collection.size()][];
        int i = 0;
        for (String string : collection) {
            strings[i++] = new String[]{string};
        }
        return strings;
    };
    public static final CriterionMerger OR = collection -> new String[][]{collection.toArray(new String[0])};

    public String[][] createRequirements(Collection<String> var1);
}

