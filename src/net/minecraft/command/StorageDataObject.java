/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 */
package net.minecraft.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class StorageDataObject
implements DataCommandObject {
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(StorageDataObject.of((CommandContext<ServerCommandSource>)commandContext).getIds(), suggestionsBuilder);
    public static final Function<String, DataCommand.ObjectType> TYPE_FACTORY = string -> new DataCommand.ObjectType((String)string){
        final /* synthetic */ String field_20859;
        {
            this.field_20859 = string;
        }

        @Override
        public DataCommandObject getObject(CommandContext<ServerCommandSource> context) {
            return new StorageDataObject(StorageDataObject.of((CommandContext<ServerCommandSource>)context), IdentifierArgumentType.getIdentifier(context, this.field_20859));
        }

        @Override
        public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ServerCommandSource, ?> argument, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> argumentAdder) {
            return argument.then(CommandManager.literal("storage").then(argumentAdder.apply((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument(this.field_20859, IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER))));
        }
    };
    private final DataCommandStorage storage;
    private final Identifier id;

    private static DataCommandStorage of(CommandContext<ServerCommandSource> commandContext) {
        return ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getDataCommandStorage();
    }

    private StorageDataObject(DataCommandStorage storage, Identifier id) {
        this.storage = storage;
        this.id = id;
    }

    @Override
    public void setNbt(NbtCompound nbt) {
        this.storage.set(this.id, nbt);
    }

    @Override
    public NbtCompound getNbt() {
        return this.storage.get(this.id);
    }

    @Override
    public Text feedbackModify() {
        return new TranslatableText("commands.data.storage.modified", this.id);
    }

    @Override
    public Text feedbackQuery(NbtElement element) {
        return new TranslatableText("commands.data.storage.query", this.id, element.toText());
    }

    @Override
    public Text feedbackGet(NbtPathArgumentType.NbtPath path, double scale, int result) {
        return new TranslatableText("commands.data.storage.get", path, this.id, String.format(Locale.ROOT, "%.2f", scale), result);
    }
}
