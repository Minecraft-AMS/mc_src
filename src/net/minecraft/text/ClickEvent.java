/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.text;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ClickEvent {
    private final Action action;
    private final String value;

    public ClickEvent(Action action, String value) {
        this.action = action;
        this.value = value;
    }

    public Action getAction() {
        return this.action;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClickEvent clickEvent = (ClickEvent)o;
        if (this.action != clickEvent.action) {
            return false;
        }
        return !(this.value != null ? !this.value.equals(clickEvent.value) : clickEvent.value != null);
    }

    public String toString() {
        return "ClickEvent{action=" + this.action + ", value='" + this.value + "'}";
    }

    public int hashCode() {
        int i = this.action.hashCode();
        i = 31 * i + (this.value != null ? this.value.hashCode() : 0);
        return i;
    }

    public static final class Action
    extends Enum<Action> {
        public static final /* enum */ Action OPEN_URL = new Action("open_url", true);
        public static final /* enum */ Action OPEN_FILE = new Action("open_file", false);
        public static final /* enum */ Action RUN_COMMAND = new Action("run_command", true);
        public static final /* enum */ Action SUGGEST_COMMAND = new Action("suggest_command", true);
        public static final /* enum */ Action CHANGE_PAGE = new Action("change_page", true);
        public static final /* enum */ Action COPY_TO_CLIPBOARD = new Action("copy_to_clipboard", true);
        private static final Map<String, Action> BY_NAME;
        private final boolean userDefinable;
        private final String name;
        private static final /* synthetic */ Action[] field_11747;

        public static Action[] values() {
            return (Action[])field_11747.clone();
        }

        public static Action valueOf(String string) {
            return Enum.valueOf(Action.class, string);
        }

        private Action(String name, boolean userDefinable) {
            this.name = name;
            this.userDefinable = userDefinable;
        }

        public boolean isUserDefinable() {
            return this.userDefinable;
        }

        public String getName() {
            return this.name;
        }

        public static Action byName(String name) {
            return BY_NAME.get(name);
        }

        private static /* synthetic */ Action[] method_36945() {
            return new Action[]{OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, CHANGE_PAGE, COPY_TO_CLIPBOARD};
        }

        static {
            field_11747 = Action.method_36945();
            BY_NAME = Arrays.stream(Action.values()).collect(Collectors.toMap(Action::getName, a -> a));
        }
    }
}

