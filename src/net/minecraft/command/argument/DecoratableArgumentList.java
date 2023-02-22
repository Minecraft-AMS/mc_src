/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.context.CommandContextBuilder
 *  com.mojang.brigadier.context.ParsedArgument
 *  com.mojang.brigadier.context.ParsedCommandNode
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.argument.DecoratableArgumentType;

public record DecoratableArgumentList<S>(List<ParsedArgument<S>> arguments) {
    public static <S> DecoratableArgumentList<S> of(ParseResults<S> parseResults) {
        CommandContextBuilder commandContextBuilder3;
        CommandContextBuilder commandContextBuilder;
        CommandContextBuilder commandContextBuilder2 = commandContextBuilder = parseResults.getContext();
        List<ParsedArgument<S>> list = DecoratableArgumentList.collectDecoratableArguments(commandContextBuilder2);
        while ((commandContextBuilder3 = commandContextBuilder2.getChild()) != null) {
            boolean bl;
            boolean bl2 = bl = commandContextBuilder3.getRootNode() != commandContextBuilder.getRootNode();
            if (!bl) break;
            list.addAll(DecoratableArgumentList.collectDecoratableArguments(commandContextBuilder3));
            commandContextBuilder2 = commandContextBuilder3;
        }
        return new DecoratableArgumentList<S>(list);
    }

    private static <S> List<ParsedArgument<S>> collectDecoratableArguments(CommandContextBuilder<S> contextBuilder) {
        ArrayList<ParsedArgument<S>> list = new ArrayList<ParsedArgument<S>>();
        for (ParsedCommandNode parsedCommandNode : contextBuilder.getNodes()) {
            ArgumentCommandNode argumentCommandNode;
            CommandNode commandNode = parsedCommandNode.getNode();
            if (!(commandNode instanceof ArgumentCommandNode) || !((commandNode = (argumentCommandNode = (ArgumentCommandNode)commandNode).getType()) instanceof DecoratableArgumentType)) continue;
            DecoratableArgumentType decoratableArgumentType = (DecoratableArgumentType)commandNode;
            com.mojang.brigadier.context.ParsedArgument parsedArgument = (com.mojang.brigadier.context.ParsedArgument)contextBuilder.getArguments().get(argumentCommandNode.getName());
            if (parsedArgument == null) continue;
            list.add(new ParsedArgument(argumentCommandNode, parsedArgument, decoratableArgumentType));
        }
        return list;
    }

    public boolean contains(CommandNode<?> node) {
        for (ParsedArgument<S> parsedArgument : this.arguments) {
            if (parsedArgument.node() != node) continue;
            return true;
        }
        return false;
    }

    public record ParsedArgument<S>(ArgumentCommandNode<S, ?> node, com.mojang.brigadier.context.ParsedArgument<S, ?> parsedValue, DecoratableArgumentType<?> argumentType) {
        public String getNodeName() {
            return this.node.getName();
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ParsedArgument.class, "node;parsedValue;previewType", "node", "parsedValue", "argumentType"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ParsedArgument.class, "node;parsedValue;previewType", "node", "parsedValue", "argumentType"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ParsedArgument.class, "node;parsedValue;previewType", "node", "parsedValue", "argumentType"}, this, object);
        }
    }
}

