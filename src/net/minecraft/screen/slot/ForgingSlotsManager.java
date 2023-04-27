/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen.slot;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;

public class ForgingSlotsManager {
    private final List<ForgingSlot> inputSlots;
    private final ForgingSlot resultSlot;

    ForgingSlotsManager(List<ForgingSlot> inputSlots, ForgingSlot resultSlot) {
        if (inputSlots.isEmpty() || resultSlot.equals(ForgingSlot.DEFAULT)) {
            throw new IllegalArgumentException("Need to define both inputSlots and resultSlot");
        }
        this.inputSlots = inputSlots;
        this.resultSlot = resultSlot;
    }

    public static Builder create() {
        return new Builder();
    }

    public boolean hasSlotIndex(int index) {
        return this.inputSlots.size() >= index;
    }

    public ForgingSlot getInputSlot(int index) {
        return this.inputSlots.get(index);
    }

    public ForgingSlot getResultSlot() {
        return this.resultSlot;
    }

    public List<ForgingSlot> getInputSlots() {
        return this.inputSlots;
    }

    public int getInputSlotCount() {
        return this.inputSlots.size();
    }

    public int getResultSlotIndex() {
        return this.getInputSlotCount();
    }

    public List<Integer> getInputSlotIndices() {
        return this.inputSlots.stream().map(ForgingSlot::slotId).collect(Collectors.toList());
    }

    public record ForgingSlot(int slotId, int x, int y, Predicate<ItemStack> mayPlace) {
        static final ForgingSlot DEFAULT = new ForgingSlot(0, 0, 0, stack -> true);

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ForgingSlot.class, "slotIndex;x;y;mayPlace", "slotId", "x", "y", "mayPlace"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ForgingSlot.class, "slotIndex;x;y;mayPlace", "slotId", "x", "y", "mayPlace"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ForgingSlot.class, "slotIndex;x;y;mayPlace", "slotId", "x", "y", "mayPlace"}, this, object);
        }
    }

    public static class Builder {
        private final List<ForgingSlot> inputSlots = new ArrayList<ForgingSlot>();
        private ForgingSlot resultSlot = ForgingSlot.DEFAULT;

        public Builder input(int slotId, int x, int y, Predicate<ItemStack> mayPlace) {
            this.inputSlots.add(new ForgingSlot(slotId, x, y, mayPlace));
            return this;
        }

        public Builder output(int slotId, int x, int y) {
            this.resultSlot = new ForgingSlot(slotId, x, y, stack -> false);
            return this;
        }

        public ForgingSlotsManager build() {
            return new ForgingSlotsManager(this.inputSlots, this.resultSlot);
        }
    }
}

