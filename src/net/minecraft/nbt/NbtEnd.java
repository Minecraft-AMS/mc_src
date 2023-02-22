/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;

public class NbtEnd
implements NbtElement {
    private static final int SIZE = 8;
    public static final NbtType<NbtEnd> TYPE = new NbtType<NbtEnd>(){

        @Override
        public NbtEnd read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) {
            nbtTagSizeTracker.add(8L);
            return INSTANCE;
        }

        @Override
        public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor) {
            return visitor.visitEnd();
        }

        @Override
        public void skip(DataInput input, int count) {
        }

        @Override
        public void skip(DataInput input) {
        }

        @Override
        public String getCrashReportName() {
            return "END";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_End";
        }

        @Override
        public boolean isImmutable() {
            return true;
        }

        @Override
        public /* synthetic */ NbtElement read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    public static final NbtEnd INSTANCE = new NbtEnd();

    private NbtEnd() {
    }

    @Override
    public void write(DataOutput output) throws IOException {
    }

    @Override
    public int getSizeInBytes() {
        return 8;
    }

    @Override
    public byte getType() {
        return 0;
    }

    public NbtType<NbtEnd> getNbtType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.asString();
    }

    @Override
    public NbtEnd copy() {
        return this;
    }

    @Override
    public void accept(NbtElementVisitor visitor) {
        visitor.visitEnd(this);
    }

    @Override
    public NbtScanner.Result doAccept(NbtScanner visitor) {
        return visitor.visitEnd();
    }

    @Override
    public /* synthetic */ NbtElement copy() {
        return this.copy();
    }
}

