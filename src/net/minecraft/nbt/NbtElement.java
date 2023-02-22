/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;

public interface NbtElement {
    public static final int field_33246 = 64;
    public static final int field_33247 = 96;
    public static final int field_33248 = 32;
    public static final int field_33249 = 224;
    public static final byte NULL_TYPE = 0;
    public static final byte BYTE_TYPE = 1;
    public static final byte SHORT_TYPE = 2;
    public static final byte INT_TYPE = 3;
    public static final byte LONG_TYPE = 4;
    public static final byte FLOAT_TYPE = 5;
    public static final byte DOUBLE_TYPE = 6;
    public static final byte BYTE_ARRAY_TYPE = 7;
    public static final byte STRING_TYPE = 8;
    public static final byte LIST_TYPE = 9;
    public static final byte COMPOUND_TYPE = 10;
    public static final byte INT_ARRAY_TYPE = 11;
    public static final byte LONG_ARRAY_TYPE = 12;
    public static final byte NUMBER_TYPE = 99;
    public static final int field_33264 = 512;

    public void write(DataOutput var1) throws IOException;

    public String toString();

    public byte getType();

    public NbtType<?> getNbtType();

    public NbtElement copy();

    default public String asString() {
        return new StringNbtWriter().apply(this);
    }

    public void accept(NbtElementVisitor var1);
}

