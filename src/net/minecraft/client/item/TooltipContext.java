/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.item;

public interface TooltipContext {
    public boolean isAdvanced();

    public static final class Default
    extends Enum<Default>
    implements TooltipContext {
        public static final /* enum */ Default NORMAL = new Default(false);
        public static final /* enum */ Default ADVANCED = new Default(true);
        private final boolean advanced;
        private static final /* synthetic */ Default[] field_8937;

        public static Default[] values() {
            return (Default[])field_8937.clone();
        }

        public static Default valueOf(String string) {
            return Enum.valueOf(Default.class, string);
        }

        private Default(boolean advanced) {
            this.advanced = advanced;
        }

        @Override
        public boolean isAdvanced() {
            return this.advanced;
        }

        private static /* synthetic */ Default[] method_36685() {
            return new Default[]{NORMAL, ADVANCED};
        }

        static {
            field_8937 = Default.method_36685();
        }
    }
}

