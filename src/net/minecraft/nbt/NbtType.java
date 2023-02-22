/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtNull;
import net.minecraft.nbt.NbtTagSizeTracker;

public interface NbtType<T extends NbtElement> {
    public T read(DataInput var1, int var2, NbtTagSizeTracker var3) throws IOException;

    default public boolean isImmutable() {
        return false;
    }

    public String getCrashReportName();

    public String getCommandFeedbackName();

    public static NbtType<NbtNull> createInvalid(final int type) {
        return new NbtType<NbtNull>(){

            @Override
            public NbtNull read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) {
                throw new IllegalArgumentException("Invalid tag id: " + type);
            }

            @Override
            public String getCrashReportName() {
                return "INVALID[" + type + "]";
            }

            @Override
            public String getCommandFeedbackName() {
                return "UNKNOWN_" + type;
            }

            @Override
            public /* synthetic */ NbtElement read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
                return this.read(input, depth, tracker);
            }
        };
    }
}

