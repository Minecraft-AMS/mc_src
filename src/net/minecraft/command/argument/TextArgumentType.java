/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TextArgumentType
implements ArgumentType<Text> {
    private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
    public static final DynamicCommandExceptionType INVALID_COMPONENT_EXCEPTION = new DynamicCommandExceptionType(text -> new TranslatableText("argument.component.invalid", text));

    private TextArgumentType() {
    }

    public static Text getTextArgument(CommandContext<ServerCommandSource> context, String name) {
        return (Text)context.getArgument(name, Text.class);
    }

    public static TextArgumentType text() {
        return new TextArgumentType();
    }

    public Text parse(StringReader stringReader) throws CommandSyntaxException {
        try {
            MutableText text = Text.Serializer.fromJson(stringReader);
            if (text == null) {
                throw INVALID_COMPONENT_EXCEPTION.createWithContext((ImmutableStringReader)stringReader, (Object)"empty");
            }
            return text;
        }
        catch (Exception exception) {
            String string = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
            throw INVALID_COMPONENT_EXCEPTION.createWithContext((ImmutableStringReader)stringReader, (Object)string);
        }
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }
}

