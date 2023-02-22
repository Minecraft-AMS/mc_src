/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

public final class ActionResult
extends Enum<ActionResult> {
    public static final /* enum */ ActionResult SUCCESS = new ActionResult();
    public static final /* enum */ ActionResult CONSUME = new ActionResult();
    public static final /* enum */ ActionResult CONSUME_PARTIAL = new ActionResult();
    public static final /* enum */ ActionResult PASS = new ActionResult();
    public static final /* enum */ ActionResult FAIL = new ActionResult();
    private static final /* synthetic */ ActionResult[] field_5813;

    public static ActionResult[] values() {
        return (ActionResult[])field_5813.clone();
    }

    public static ActionResult valueOf(String string) {
        return Enum.valueOf(ActionResult.class, string);
    }

    public boolean isAccepted() {
        return this == SUCCESS || this == CONSUME || this == CONSUME_PARTIAL;
    }

    public boolean shouldSwingHand() {
        return this == SUCCESS;
    }

    public boolean shouldIncrementStat() {
        return this == SUCCESS || this == CONSUME;
    }

    public static ActionResult success(boolean swingHand) {
        return swingHand ? SUCCESS : CONSUME;
    }

    private static /* synthetic */ ActionResult[] method_36599() {
        return new ActionResult[]{SUCCESS, CONSUME, CONSUME_PARTIAL, PASS, FAIL};
    }

    static {
        field_5813 = ActionResult.method_36599();
    }
}

