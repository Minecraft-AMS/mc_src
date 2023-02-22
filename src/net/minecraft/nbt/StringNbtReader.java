/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.TranslatableText;

public class StringNbtReader {
    public static final SimpleCommandExceptionType TRAILING = new SimpleCommandExceptionType((Message)new TranslatableText("argument.nbt.trailing", new Object[0]));
    public static final SimpleCommandExceptionType EXPECTED_KEY = new SimpleCommandExceptionType((Message)new TranslatableText("argument.nbt.expected.key", new Object[0]));
    public static final SimpleCommandExceptionType EXPECTED_VALUE = new SimpleCommandExceptionType((Message)new TranslatableText("argument.nbt.expected.value", new Object[0]));
    public static final Dynamic2CommandExceptionType LIST_MIXED = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("argument.nbt.list.mixed", object, object2));
    public static final Dynamic2CommandExceptionType ARRAY_MIXED = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("argument.nbt.array.mixed", object, object2));
    public static final DynamicCommandExceptionType ARRAY_INVALID = new DynamicCommandExceptionType(object -> new TranslatableText("argument.nbt.array.invalid", object));
    private static final Pattern DOUBLE_PATTERN_IMPLICIT = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
    private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
    private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
    private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
    private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    private final StringReader reader;

    public static CompoundTag parse(String string) throws CommandSyntaxException {
        return new StringNbtReader(new StringReader(string)).readCompoundTag();
    }

    @VisibleForTesting
    CompoundTag readCompoundTag() throws CommandSyntaxException {
        CompoundTag compoundTag = this.parseCompoundTag();
        this.reader.skipWhitespace();
        if (this.reader.canRead()) {
            throw TRAILING.createWithContext((ImmutableStringReader)this.reader);
        }
        return compoundTag;
    }

    public StringNbtReader(StringReader stringReader) {
        this.reader = stringReader;
    }

    protected String readString() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw EXPECTED_KEY.createWithContext((ImmutableStringReader)this.reader);
        }
        return this.reader.readString();
    }

    protected Tag parseTagPrimitive() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        int i = this.reader.getCursor();
        if (StringReader.isQuotedStringStart((char)this.reader.peek())) {
            return new StringTag(this.reader.readQuotedString());
        }
        String string = this.reader.readUnquotedString();
        if (string.isEmpty()) {
            this.reader.setCursor(i);
            throw EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        return this.parsePrimitive(string);
    }

    private Tag parsePrimitive(String input) {
        try {
            if (FLOAT_PATTERN.matcher(input).matches()) {
                return new FloatTag(Float.parseFloat(input.substring(0, input.length() - 1)));
            }
            if (BYTE_PATTERN.matcher(input).matches()) {
                return new ByteTag(Byte.parseByte(input.substring(0, input.length() - 1)));
            }
            if (LONG_PATTERN.matcher(input).matches()) {
                return new LongTag(Long.parseLong(input.substring(0, input.length() - 1)));
            }
            if (SHORT_PATTERN.matcher(input).matches()) {
                return new ShortTag(Short.parseShort(input.substring(0, input.length() - 1)));
            }
            if (INT_PATTERN.matcher(input).matches()) {
                return new IntTag(Integer.parseInt(input));
            }
            if (DOUBLE_PATTERN.matcher(input).matches()) {
                return new DoubleTag(Double.parseDouble(input.substring(0, input.length() - 1)));
            }
            if (DOUBLE_PATTERN_IMPLICIT.matcher(input).matches()) {
                return new DoubleTag(Double.parseDouble(input));
            }
            if ("true".equalsIgnoreCase(input)) {
                return new ByteTag(1);
            }
            if ("false".equalsIgnoreCase(input)) {
                return new ByteTag(0);
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return new StringTag(input);
    }

    public Tag parseTag() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        char c = this.reader.peek();
        if (c == '{') {
            return this.parseCompoundTag();
        }
        if (c == '[') {
            return this.parseTagArray();
        }
        return this.parseTagPrimitive();
    }

    protected Tag parseTagArray() throws CommandSyntaxException {
        if (this.reader.canRead(3) && !StringReader.isQuotedStringStart((char)this.reader.peek(1)) && this.reader.peek(2) == ';') {
            return this.parseTagPrimitiveArray();
        }
        return this.parseListTag();
    }

    public CompoundTag parseCompoundTag() throws CommandSyntaxException {
        this.expect('{');
        CompoundTag compoundTag = new CompoundTag();
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != '}') {
            int i = this.reader.getCursor();
            String string = this.readString();
            if (string.isEmpty()) {
                this.reader.setCursor(i);
                throw EXPECTED_KEY.createWithContext((ImmutableStringReader)this.reader);
            }
            this.expect(':');
            compoundTag.put(string, this.parseTag());
            if (!this.readComma()) break;
            if (this.reader.canRead()) continue;
            throw EXPECTED_KEY.createWithContext((ImmutableStringReader)this.reader);
        }
        this.expect('}');
        return compoundTag;
    }

    private Tag parseListTag() throws CommandSyntaxException {
        this.expect('[');
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        ListTag listTag = new ListTag();
        byte i = -1;
        while (this.reader.peek() != ']') {
            int j = this.reader.getCursor();
            Tag tag = this.parseTag();
            byte k = tag.getType();
            if (i < 0) {
                i = k;
            } else if (k != i) {
                this.reader.setCursor(j);
                throw LIST_MIXED.createWithContext((ImmutableStringReader)this.reader, (Object)Tag.idToString(k), (Object)Tag.idToString(i));
            }
            listTag.add(tag);
            if (!this.readComma()) break;
            if (this.reader.canRead()) continue;
            throw EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        this.expect(']');
        return listTag;
    }

    private Tag parseTagPrimitiveArray() throws CommandSyntaxException {
        this.expect('[');
        int i = this.reader.getCursor();
        char c = this.reader.read();
        this.reader.read();
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        if (c == 'B') {
            return new ByteArrayTag(this.readArray((byte)7, (byte)1));
        }
        if (c == 'L') {
            return new LongArrayTag(this.readArray((byte)12, (byte)4));
        }
        if (c == 'I') {
            return new IntArrayTag(this.readArray((byte)11, (byte)3));
        }
        this.reader.setCursor(i);
        throw ARRAY_INVALID.createWithContext((ImmutableStringReader)this.reader, (Object)String.valueOf(c));
    }

    private <T extends Number> List<T> readArray(byte b, byte c) throws CommandSyntaxException {
        ArrayList list = Lists.newArrayList();
        while (this.reader.peek() != ']') {
            int i = this.reader.getCursor();
            Tag tag = this.parseTag();
            byte j = tag.getType();
            if (j != c) {
                this.reader.setCursor(i);
                throw ARRAY_MIXED.createWithContext((ImmutableStringReader)this.reader, (Object)Tag.idToString(j), (Object)Tag.idToString(b));
            }
            if (c == 1) {
                list.add(((AbstractNumberTag)tag).getByte());
            } else if (c == 4) {
                list.add(((AbstractNumberTag)tag).getLong());
            } else {
                list.add(((AbstractNumberTag)tag).getInt());
            }
            if (!this.readComma()) break;
            if (this.reader.canRead()) continue;
            throw EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        this.expect(']');
        return list;
    }

    private boolean readComma() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == ',') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }
        return false;
    }

    private void expect(char c) throws CommandSyntaxException {
        this.reader.skipWhitespace();
        this.reader.expect(c);
    }
}

