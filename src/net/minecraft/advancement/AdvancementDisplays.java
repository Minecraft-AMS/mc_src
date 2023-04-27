/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Stack
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.advancement;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Predicate;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;

public class AdvancementDisplays {
    private static final int DISPLAY_DEPTH = 2;

    private static Status getStatus(Advancement advancement, boolean force) {
        AdvancementDisplay advancementDisplay = advancement.getDisplay();
        if (advancementDisplay == null) {
            return Status.HIDE;
        }
        if (force) {
            return Status.SHOW;
        }
        if (advancementDisplay.isHidden()) {
            return Status.HIDE;
        }
        return Status.NO_CHANGE;
    }

    private static boolean shouldDisplay(Stack<Status> statuses) {
        for (int i = 0; i <= 2; ++i) {
            Status status = (Status)((Object)statuses.peek(i));
            if (status == Status.SHOW) {
                return true;
            }
            if (status != Status.HIDE) continue;
            return false;
        }
        return false;
    }

    private static boolean shouldDisplay(Advancement advancement, Stack<Status> statuses, Predicate<Advancement> donePredicate, ResultConsumer consumer) {
        boolean bl = donePredicate.test(advancement);
        Status status = AdvancementDisplays.getStatus(advancement, bl);
        boolean bl2 = bl;
        statuses.push((Object)status);
        for (Advancement advancement2 : advancement.getChildren()) {
            bl2 |= AdvancementDisplays.shouldDisplay(advancement2, statuses, donePredicate, consumer);
        }
        boolean bl3 = bl2 || AdvancementDisplays.shouldDisplay(statuses);
        statuses.pop();
        consumer.accept(advancement, bl3);
        return bl2;
    }

    public static void calculateDisplay(Advancement advancement, Predicate<Advancement> donePredicate, ResultConsumer consumer) {
        Advancement advancement2 = advancement.getRoot();
        ObjectArrayList stack = new ObjectArrayList();
        for (int i = 0; i <= 2; ++i) {
            stack.push((Object)Status.NO_CHANGE);
        }
        AdvancementDisplays.shouldDisplay(advancement2, (Stack<Status>)stack, donePredicate, consumer);
    }

    static final class Status
    extends Enum<Status> {
        public static final /* enum */ Status SHOW = new Status();
        public static final /* enum */ Status HIDE = new Status();
        public static final /* enum */ Status NO_CHANGE = new Status();
        private static final /* synthetic */ Status[] field_41741;

        public static Status[] values() {
            return (Status[])field_41741.clone();
        }

        public static Status valueOf(String string) {
            return Enum.valueOf(Status.class, string);
        }

        private static /* synthetic */ Status[] method_48034() {
            return new Status[]{SHOW, HIDE, NO_CHANGE};
        }

        static {
            field_41741 = Status.method_48034();
        }
    }

    @FunctionalInterface
    public static interface ResultConsumer {
        public void accept(Advancement var1, boolean var2);
    }
}

