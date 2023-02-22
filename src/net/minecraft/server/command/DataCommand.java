/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.command.BlockDataObject;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.EntityDataObject;
import net.minecraft.command.StorageDataObject;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.argument.NbtElementArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class DataCommand {
    private static final SimpleCommandExceptionType MERGE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("commands.data.merge.failed"));
    private static final DynamicCommandExceptionType GET_INVALID_EXCEPTION = new DynamicCommandExceptionType(path -> Text.translatable("commands.data.get.invalid", path));
    private static final DynamicCommandExceptionType GET_UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(path -> Text.translatable("commands.data.get.unknown", path));
    private static final SimpleCommandExceptionType GET_MULTIPLE_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("commands.data.get.multiple"));
    private static final DynamicCommandExceptionType MODIFY_EXPECTED_LIST_EXCEPTION = new DynamicCommandExceptionType(nbt -> Text.translatable("commands.data.modify.expected_list", nbt));
    private static final DynamicCommandExceptionType MODIFY_EXPECTED_OBJECT_EXCEPTION = new DynamicCommandExceptionType(nbt -> Text.translatable("commands.data.modify.expected_object", nbt));
    private static final DynamicCommandExceptionType MODIFY_INVALID_INDEX_EXCEPTION = new DynamicCommandExceptionType(index -> Text.translatable("commands.data.modify.invalid_index", index));
    public static final List<Function<String, ObjectType>> OBJECT_TYPE_FACTORIES = ImmutableList.of(EntityDataObject.TYPE_FACTORY, BlockDataObject.TYPE_FACTORY, StorageDataObject.TYPE_FACTORY);
    public static final List<ObjectType> TARGET_OBJECT_TYPES = (List)OBJECT_TYPE_FACTORIES.stream().map(factory -> (ObjectType)factory.apply("target")).collect(ImmutableList.toImmutableList());
    public static final List<ObjectType> SOURCE_OBJECT_TYPES = (List)OBJECT_TYPE_FACTORIES.stream().map(factory -> (ObjectType)factory.apply("source")).collect(ImmutableList.toImmutableList());

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("data").requires(source -> source.hasPermissionLevel(2));
        for (ObjectType objectType : TARGET_OBJECT_TYPES) {
            ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.then(objectType.addArgumentsToBuilder((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("merge"), builder -> builder.then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound()).executes(context -> DataCommand.executeMerge((ServerCommandSource)context.getSource(), objectType.getObject((CommandContext<ServerCommandSource>)context), NbtCompoundArgumentType.getNbtCompound(context, "nbt"))))))).then(objectType.addArgumentsToBuilder((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("get"), builder -> builder.executes(context -> DataCommand.executeGet((ServerCommandSource)context.getSource(), objectType.getObject((CommandContext<ServerCommandSource>)context))).then(((RequiredArgumentBuilder)CommandManager.argument("path", NbtPathArgumentType.nbtPath()).executes(context -> DataCommand.executeGet((ServerCommandSource)context.getSource(), objectType.getObject((CommandContext<ServerCommandSource>)context), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)context, "path")))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(context -> DataCommand.executeGet((ServerCommandSource)context.getSource(), objectType.getObject((CommandContext<ServerCommandSource>)context), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)context, "path"), DoubleArgumentType.getDouble((CommandContext)context, (String)"scale")))))))).then(objectType.addArgumentsToBuilder((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("remove"), builder -> builder.then(CommandManager.argument("path", NbtPathArgumentType.nbtPath()).executes(context -> DataCommand.executeRemove((ServerCommandSource)context.getSource(), objectType.getObject((CommandContext<ServerCommandSource>)context), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)context, "path"))))))).then(DataCommand.addModifyArgument((builder, modifier) -> builder.then(CommandManager.literal("insert").then(CommandManager.argument("index", IntegerArgumentType.integer()).then(modifier.create((context, sourceNbt, path, elements) -> {
                int i = IntegerArgumentType.getInteger((CommandContext)context, (String)"index");
                return DataCommand.executeInsert(i, sourceNbt, path, elements);
            })))).then(CommandManager.literal("prepend").then(modifier.create((context, sourceNbt, path, elements) -> DataCommand.executeInsert(0, sourceNbt, path, elements)))).then(CommandManager.literal("append").then(modifier.create((context, sourceNbt, path, elements) -> DataCommand.executeInsert(-1, sourceNbt, path, elements)))).then(CommandManager.literal("set").then(modifier.create((context, sourceNbt, path, elements) -> path.put((NbtElement)sourceNbt, ((NbtElement)Iterables.getLast((Iterable)elements))::copy)))).then(CommandManager.literal("merge").then(modifier.create((context, sourceNbt, path, elements) -> {
                List<NbtElement> collection = path.getOrInit(sourceNbt, NbtCompound::new);
                int i = 0;
                for (NbtElement nbtElement : collection) {
                    if (!(nbtElement instanceof NbtCompound)) {
                        throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create((Object)nbtElement);
                    }
                    NbtCompound nbtCompound = (NbtCompound)nbtElement;
                    NbtCompound nbtCompound2 = nbtCompound.copy();
                    for (NbtElement nbtElement2 : elements) {
                        if (!(nbtElement2 instanceof NbtCompound)) {
                            throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create((Object)nbtElement2);
                        }
                        nbtCompound.copyFrom((NbtCompound)nbtElement2);
                    }
                    i += nbtCompound2.equals(nbtCompound) ? 0 : 1;
                }
                return i;
            })))));
        }
        dispatcher.register(literalArgumentBuilder);
    }

    private static int executeInsert(int integer, NbtCompound sourceNbt, NbtPathArgumentType.NbtPath path, List<NbtElement> elements) throws CommandSyntaxException {
        List<NbtElement> collection = path.getOrInit(sourceNbt, NbtList::new);
        int i = 0;
        for (NbtElement nbtElement : collection) {
            if (!(nbtElement instanceof AbstractNbtList)) {
                throw MODIFY_EXPECTED_LIST_EXCEPTION.create((Object)nbtElement);
            }
            boolean bl = false;
            AbstractNbtList abstractNbtList = (AbstractNbtList)nbtElement;
            int j = integer < 0 ? abstractNbtList.size() + integer + 1 : integer;
            for (NbtElement nbtElement2 : elements) {
                try {
                    if (!abstractNbtList.addElement(j, nbtElement2.copy())) continue;
                    ++j;
                    bl = true;
                }
                catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    throw MODIFY_INVALID_INDEX_EXCEPTION.create((Object)j);
                }
            }
            i += bl ? 1 : 0;
        }
        return i;
    }

    private static ArgumentBuilder<ServerCommandSource, ?> addModifyArgument(BiConsumer<ArgumentBuilder<ServerCommandSource, ?>, ModifyArgumentCreator> subArgumentAdder) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("modify");
        for (ObjectType objectType : TARGET_OBJECT_TYPES) {
            objectType.addArgumentsToBuilder((ArgumentBuilder<ServerCommandSource, ?>)literalArgumentBuilder, builder -> {
                RequiredArgumentBuilder<ServerCommandSource, NbtPathArgumentType.NbtPath> argumentBuilder = CommandManager.argument("targetPath", NbtPathArgumentType.nbtPath());
                for (ObjectType objectType2 : SOURCE_OBJECT_TYPES) {
                    subArgumentAdder.accept((ArgumentBuilder<ServerCommandSource, ?>)argumentBuilder, modifier -> objectType2.addArgumentsToBuilder((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("from"), builder -> builder.executes(context -> {
                        List<NbtElement> list = Collections.singletonList(objectType2.getObject((CommandContext<ServerCommandSource>)context).getNbt());
                        return DataCommand.executeModify((CommandContext<ServerCommandSource>)context, objectType, modifier, list);
                    }).then(CommandManager.argument("sourcePath", NbtPathArgumentType.nbtPath()).executes(context -> {
                        DataCommandObject dataCommandObject = objectType2.getObject((CommandContext<ServerCommandSource>)context);
                        NbtPathArgumentType.NbtPath nbtPath = NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)context, "sourcePath");
                        List<NbtElement> list = nbtPath.get(dataCommandObject.getNbt());
                        return DataCommand.executeModify((CommandContext<ServerCommandSource>)context, objectType, modifier, list);
                    }))));
                }
                subArgumentAdder.accept((ArgumentBuilder<ServerCommandSource, ?>)argumentBuilder, modifier -> CommandManager.literal("value").then(CommandManager.argument("value", NbtElementArgumentType.nbtElement()).executes(context -> {
                    List<NbtElement> list = Collections.singletonList(NbtElementArgumentType.getNbtElement(context, "value"));
                    return DataCommand.executeModify((CommandContext<ServerCommandSource>)context, objectType, modifier, list);
                })));
                return builder.then(argumentBuilder);
            });
        }
        return literalArgumentBuilder;
    }

    private static int executeModify(CommandContext<ServerCommandSource> context, ObjectType objectType, ModifyOperation modifier, List<NbtElement> elements) throws CommandSyntaxException {
        DataCommandObject dataCommandObject = objectType.getObject(context);
        NbtPathArgumentType.NbtPath nbtPath = NbtPathArgumentType.getNbtPath(context, "targetPath");
        NbtCompound nbtCompound = dataCommandObject.getNbt();
        int i = modifier.modify(context, nbtCompound, nbtPath, elements);
        if (i == 0) {
            throw MERGE_FAILED_EXCEPTION.create();
        }
        dataCommandObject.setNbt(nbtCompound);
        ((ServerCommandSource)context.getSource()).sendFeedback(dataCommandObject.feedbackModify(), true);
        return i;
    }

    private static int executeRemove(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
        NbtCompound nbtCompound = object.getNbt();
        int i = path.remove(nbtCompound);
        if (i == 0) {
            throw MERGE_FAILED_EXCEPTION.create();
        }
        object.setNbt(nbtCompound);
        source.sendFeedback(object.feedbackModify(), true);
        return i;
    }

    private static NbtElement getNbt(NbtPathArgumentType.NbtPath path, DataCommandObject object) throws CommandSyntaxException {
        List<NbtElement> collection = path.get(object.getNbt());
        Iterator iterator = collection.iterator();
        NbtElement nbtElement = (NbtElement)iterator.next();
        if (iterator.hasNext()) {
            throw GET_MULTIPLE_EXCEPTION.create();
        }
        return nbtElement;
    }

    private static int executeGet(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
        int i;
        NbtElement nbtElement = DataCommand.getNbt(path, object);
        if (nbtElement instanceof AbstractNbtNumber) {
            i = MathHelper.floor(((AbstractNbtNumber)nbtElement).doubleValue());
        } else if (nbtElement instanceof AbstractNbtList) {
            i = ((AbstractNbtList)nbtElement).size();
        } else if (nbtElement instanceof NbtCompound) {
            i = ((NbtCompound)nbtElement).getSize();
        } else if (nbtElement instanceof NbtString) {
            i = nbtElement.asString().length();
        } else {
            throw GET_UNKNOWN_EXCEPTION.create((Object)path.toString());
        }
        source.sendFeedback(object.feedbackQuery(nbtElement), false);
        return i;
    }

    private static int executeGet(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path, double scale) throws CommandSyntaxException {
        NbtElement nbtElement = DataCommand.getNbt(path, object);
        if (!(nbtElement instanceof AbstractNbtNumber)) {
            throw GET_INVALID_EXCEPTION.create((Object)path.toString());
        }
        int i = MathHelper.floor(((AbstractNbtNumber)nbtElement).doubleValue() * scale);
        source.sendFeedback(object.feedbackGet(path, scale, i), false);
        return i;
    }

    private static int executeGet(ServerCommandSource source, DataCommandObject object) throws CommandSyntaxException {
        source.sendFeedback(object.feedbackQuery(object.getNbt()), false);
        return 1;
    }

    private static int executeMerge(ServerCommandSource source, DataCommandObject object, NbtCompound nbt) throws CommandSyntaxException {
        NbtCompound nbtCompound2;
        NbtCompound nbtCompound = object.getNbt();
        if (nbtCompound.equals(nbtCompound2 = nbtCompound.copy().copyFrom(nbt))) {
            throw MERGE_FAILED_EXCEPTION.create();
        }
        object.setNbt(nbtCompound2);
        source.sendFeedback(object.feedbackModify(), true);
        return 1;
    }

    public static interface ObjectType {
        public DataCommandObject getObject(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;

        public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ServerCommandSource, ?> var1, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> var2);
    }

    static interface ModifyOperation {
        public int modify(CommandContext<ServerCommandSource> var1, NbtCompound var2, NbtPathArgumentType.NbtPath var3, List<NbtElement> var4) throws CommandSyntaxException;
    }

    static interface ModifyArgumentCreator {
        public ArgumentBuilder<ServerCommandSource, ?> create(ModifyOperation var1);
    }
}

