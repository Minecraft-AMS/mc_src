/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.item;

public interface TooltipContext {
    public static final Default BASIC = new Default(false, false);
    public static final Default ADVANCED = new Default(true, false);

    public boolean isAdvanced();

    public boolean isCreative();

    public record Default(boolean advanced, boolean creative) implements TooltipContext
    {
        @Override
        public boolean isAdvanced() {
            return this.advanced;
        }

        @Override
        public boolean isCreative() {
            return this.creative;
        }

        public Default withCreative() {
            return new Default(this.advanced, true);
        }
    }
}

