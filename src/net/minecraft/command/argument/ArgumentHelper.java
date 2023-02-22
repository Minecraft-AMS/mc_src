/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.command.argument;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.registry.Registries;
import org.slf4j.Logger;

public class ArgumentHelper {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final byte MIN_FLAG = 1;
    private static final byte MAX_FLAG = 2;

    public static int getMinMaxFlag(boolean hasMin, boolean hasMax) {
        int i = 0;
        if (hasMin) {
            i |= 1;
        }
        if (hasMax) {
            i |= 2;
        }
        return i;
    }

    public static boolean hasMinFlag(byte flags) {
        return (flags & 1) != 0;
    }

    public static boolean hasMaxFlag(byte flags) {
        return (flags & 2) != 0;
    }

    private static <A extends ArgumentType<?>> void writeArgumentProperties(JsonObject json, ArgumentSerializer.ArgumentTypeProperties<A> properties) {
        ArgumentHelper.writeArgumentProperties(json, properties.getSerializer(), properties);
    }

    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> void writeArgumentProperties(JsonObject json, ArgumentSerializer<A, T> serializer, ArgumentSerializer.ArgumentTypeProperties<A> properties) {
        serializer.writeJson(properties, json);
    }

    private static <T extends ArgumentType<?>> void writeArgument(JsonObject json, T argumentType) {
        ArgumentSerializer.ArgumentTypeProperties<T> argumentTypeProperties = ArgumentTypes.getArgumentTypeProperties(argumentType);
        json.addProperty("type", "argument");
        json.addProperty("parser", Registries.COMMAND_ARGUMENT_TYPE.getId(argumentTypeProperties.getSerializer()).toString());
        JsonObject jsonObject = new JsonObject();
        ArgumentHelper.writeArgumentProperties(jsonObject, argumentTypeProperties);
        if (jsonObject.size() > 0) {
            json.add("properties", (JsonElement)jsonObject);
        }
    }

    public static <S> JsonObject toJson(CommandDispatcher<S> dispatcher, CommandNode<S> rootNode) {
        Collection collection;
        JsonObject jsonObject = new JsonObject();
        if (rootNode instanceof RootCommandNode) {
            jsonObject.addProperty("type", "root");
        } else if (rootNode instanceof LiteralCommandNode) {
            jsonObject.addProperty("type", "literal");
        } else if (rootNode instanceof ArgumentCommandNode) {
            ArgumentCommandNode argumentCommandNode = (ArgumentCommandNode)rootNode;
            ArgumentHelper.writeArgument(jsonObject, argumentCommandNode.getType());
        } else {
            LOGGER.error("Could not serialize node {} ({})!", rootNode, rootNode.getClass());
            jsonObject.addProperty("type", "unknown");
        }
        JsonObject jsonObject2 = new JsonObject();
        for (CommandNode commandNode : rootNode.getChildren()) {
            jsonObject2.add(commandNode.getName(), (JsonElement)ArgumentHelper.toJson(dispatcher, commandNode));
        }
        if (jsonObject2.size() > 0) {
            jsonObject.add("children", (JsonElement)jsonObject2);
        }
        if (rootNode.getCommand() != null) {
            jsonObject.addProperty("executable", Boolean.valueOf(true));
        }
        if (rootNode.getRedirect() != null && !(collection = dispatcher.getPath(rootNode.getRedirect())).isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            for (String string : collection) {
                jsonArray.add(string);
            }
            jsonObject.add("redirect", (JsonElement)jsonArray);
        }
        return jsonObject;
    }

    public static <T> Set<ArgumentType<?>> collectUsedArgumentTypes(CommandNode<T> rootNode) {
        Set set = Sets.newIdentityHashSet();
        HashSet set2 = Sets.newHashSet();
        ArgumentHelper.collectUsedArgumentTypes(rootNode, set2, set);
        return set2;
    }

    private static <T> void collectUsedArgumentTypes(CommandNode<T> node, Set<ArgumentType<?>> usedArgumentTypes, Set<CommandNode<T>> visitedNodes) {
        if (!visitedNodes.add(node)) {
            return;
        }
        if (node instanceof ArgumentCommandNode) {
            ArgumentCommandNode argumentCommandNode = (ArgumentCommandNode)node;
            usedArgumentTypes.add(argumentCommandNode.getType());
        }
        node.getChildren().forEach(child -> ArgumentHelper.collectUsedArgumentTypes(child, usedArgumentTypes, visitedNodes));
        CommandNode commandNode = node.getRedirect();
        if (commandNode != null) {
            ArgumentHelper.collectUsedArgumentTypes(commandNode, usedArgumentTypes, visitedNodes);
        }
    }
}

