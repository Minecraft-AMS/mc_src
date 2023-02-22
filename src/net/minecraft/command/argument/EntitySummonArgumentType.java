/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntitySummonArgumentType
implements ArgumentType<Identifier> {
    private static final Collection<String> EXAMPLES = Arrays.asList("minecraft:pig", "cow");
    public static final DynamicCommandExceptionType NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("entity.notFound", id));

    public static EntitySummonArgumentType entitySummon() {
        return new EntitySummonArgumentType();
    }

    public static Identifier getEntitySummon(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return EntitySummonArgumentType.validate((Identifier)context.getArgument(name, Identifier.class));
    }

    private static Identifier validate(Identifier id) throws CommandSyntaxException {
        Registry.ENTITY_TYPE.getOrEmpty(id).filter(EntityType::isSummonable).orElseThrow(() -> NOT_FOUND_EXCEPTION.create((Object)id));
        return id;
    }

    public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
        return EntitySummonArgumentType.validate(Identifier.fromCommandInput(stringReader));
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }
}

