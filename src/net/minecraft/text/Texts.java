/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.text;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.ParsableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class Texts {
    public static Text setStyleIfAbsent(Text text, Style style) {
        if (style.isEmpty()) {
            return text;
        }
        if (text.getStyle().isEmpty()) {
            return text.setStyle(style.deepCopy());
        }
        return new LiteralText("").append(text).setStyle(style.deepCopy());
    }

    public static Text parse(@Nullable ServerCommandSource source, Text text, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        if (depth > 100) {
            return text;
        }
        Text text2 = text instanceof ParsableText ? ((ParsableText)((Object)text)).parse(source, sender, ++depth) : text.copy();
        for (Text text3 : text.getSiblings()) {
            text2.append(Texts.parse(source, text3, sender, depth));
        }
        return Texts.setStyleIfAbsent(text2, text.getStyle());
    }

    public static Text toText(GameProfile profile) {
        if (profile.getName() != null) {
            return new LiteralText(profile.getName());
        }
        if (profile.getId() != null) {
            return new LiteralText(profile.getId().toString());
        }
        return new LiteralText("(unknown)");
    }

    public static Text joinOrdered(Collection<String> strings) {
        return Texts.joinOrdered(strings, string -> new LiteralText((String)string).formatted(Formatting.GREEN));
    }

    public static <T extends Comparable<T>> Text joinOrdered(Collection<T> elements, Function<T, Text> transformer) {
        if (elements.isEmpty()) {
            return new LiteralText("");
        }
        if (elements.size() == 1) {
            return transformer.apply(elements.iterator().next());
        }
        ArrayList list = Lists.newArrayList(elements);
        list.sort(Comparable::compareTo);
        return Texts.join(elements, transformer);
    }

    public static <T> Text join(Collection<T> elements, Function<T, Text> transformer) {
        if (elements.isEmpty()) {
            return new LiteralText("");
        }
        if (elements.size() == 1) {
            return transformer.apply(elements.iterator().next());
        }
        LiteralText text = new LiteralText("");
        boolean bl = true;
        for (T object : elements) {
            if (!bl) {
                text.append(new LiteralText(", ").formatted(Formatting.GRAY));
            }
            text.append(transformer.apply(object));
            bl = false;
        }
        return text;
    }

    public static Text bracketed(Text text) {
        return new LiteralText("[").append(text).append("]");
    }

    public static Text toText(Message message) {
        if (message instanceof Text) {
            return (Text)message;
        }
        return new LiteralText(message.getString());
    }
}

