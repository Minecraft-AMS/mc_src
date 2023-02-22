/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.ints.IntSets
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Queues;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayDeque;
import java.util.List;
import java.util.function.BiPredicate;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class CommandTreeS2CPacket
implements Packet<ClientPlayPacketListener> {
    private static final byte field_33317 = 3;
    private static final byte field_33318 = 4;
    private static final byte field_33319 = 8;
    private static final byte field_33320 = 16;
    private static final byte field_33321 = 0;
    private static final byte field_33322 = 1;
    private static final byte field_33323 = 2;
    private final int rootSize;
    private final List<CommandNodeData> nodes;

    public CommandTreeS2CPacket(RootCommandNode<CommandSource> rootNode) {
        Object2IntMap<CommandNode<CommandSource>> object2IntMap = CommandTreeS2CPacket.traverse(rootNode);
        this.nodes = CommandTreeS2CPacket.collectNodes(object2IntMap);
        this.rootSize = object2IntMap.getInt(rootNode);
    }

    public CommandTreeS2CPacket(PacketByteBuf buf) {
        this.nodes = buf.readList(CommandTreeS2CPacket::readCommandNode);
        this.rootSize = buf.readVarInt();
        CommandTreeS2CPacket.validate(this.nodes);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeCollection(this.nodes, (buf2, node) -> node.write((PacketByteBuf)((Object)buf2)));
        buf.writeVarInt(this.rootSize);
    }

    private static void validate(List<CommandNodeData> nodeDatas, BiPredicate<CommandNodeData, IntSet> validator) {
        IntOpenHashSet intSet = new IntOpenHashSet((IntCollection)IntSets.fromTo((int)0, (int)nodeDatas.size()));
        while (!intSet.isEmpty()) {
            boolean bl = intSet.removeIf(arg_0 -> CommandTreeS2CPacket.method_42068(validator, nodeDatas, (IntSet)intSet, arg_0));
            if (bl) continue;
            throw new IllegalStateException("Server sent an impossible command tree");
        }
    }

    private static void validate(List<CommandNodeData> nodeDatas) {
        CommandTreeS2CPacket.validate(nodeDatas, CommandNodeData::validateRedirectNodeIndex);
        CommandTreeS2CPacket.validate(nodeDatas, CommandNodeData::validateChildNodeIndices);
    }

    private static Object2IntMap<CommandNode<CommandSource>> traverse(RootCommandNode<CommandSource> commandTree) {
        CommandNode commandNode;
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        ArrayDeque queue = Queues.newArrayDeque();
        queue.add(commandTree);
        while ((commandNode = (CommandNode)queue.poll()) != null) {
            if (object2IntMap.containsKey((Object)commandNode)) continue;
            int i = object2IntMap.size();
            object2IntMap.put((Object)commandNode, i);
            queue.addAll(commandNode.getChildren());
            if (commandNode.getRedirect() == null) continue;
            queue.add(commandNode.getRedirect());
        }
        return object2IntMap;
    }

    private static List<CommandNodeData> collectNodes(Object2IntMap<CommandNode<CommandSource>> nodes) {
        ObjectArrayList objectArrayList = new ObjectArrayList(nodes.size());
        objectArrayList.size(nodes.size());
        for (Object2IntMap.Entry entry : Object2IntMaps.fastIterable(nodes)) {
            objectArrayList.set(entry.getIntValue(), (Object)CommandTreeS2CPacket.createNodeData((CommandNode<CommandSource>)((CommandNode)entry.getKey()), nodes));
        }
        return objectArrayList;
    }

    private static CommandNodeData readCommandNode(PacketByteBuf buf) {
        byte b = buf.readByte();
        int[] is = buf.readIntArray();
        int i = (b & 8) != 0 ? buf.readVarInt() : 0;
        SuggestableNode suggestableNode = CommandTreeS2CPacket.readArgumentBuilder(buf, b);
        return new CommandNodeData(suggestableNode, b, i, is);
    }

    @Nullable
    private static SuggestableNode readArgumentBuilder(PacketByteBuf buf, byte flags) {
        int i = flags & 3;
        if (i == 2) {
            String string = buf.readString();
            int j = buf.readVarInt();
            ArgumentSerializer argumentSerializer = (ArgumentSerializer)Registry.COMMAND_ARGUMENT_TYPE.get(j);
            if (argumentSerializer == null) {
                return null;
            }
            Object argumentTypeProperties = argumentSerializer.fromPacket(buf);
            Identifier identifier = (flags & 0x10) != 0 ? buf.readIdentifier() : null;
            return new ArgumentNode(string, (ArgumentSerializer.ArgumentTypeProperties<?>)argumentTypeProperties, identifier);
        }
        if (i == 1) {
            String string = buf.readString();
            return new LiteralNode(string);
        }
        return null;
    }

    private static CommandNodeData createNodeData(CommandNode<CommandSource> node, Object2IntMap<CommandNode<CommandSource>> nodes) {
        SuggestableNode suggestableNode;
        int j;
        int i = 0;
        if (node.getRedirect() != null) {
            i |= 8;
            j = nodes.getInt((Object)node.getRedirect());
        } else {
            j = 0;
        }
        if (node.getCommand() != null) {
            i |= 4;
        }
        if (node instanceof RootCommandNode) {
            i |= 0;
            suggestableNode = null;
        } else if (node instanceof ArgumentCommandNode) {
            ArgumentCommandNode argumentCommandNode = (ArgumentCommandNode)node;
            suggestableNode = new ArgumentNode(argumentCommandNode);
            i |= 2;
            if (argumentCommandNode.getCustomSuggestions() != null) {
                i |= 0x10;
            }
        } else if (node instanceof LiteralCommandNode) {
            LiteralCommandNode literalCommandNode = (LiteralCommandNode)node;
            suggestableNode = new LiteralNode(literalCommandNode.getLiteral());
            i |= 1;
        } else {
            throw new UnsupportedOperationException("Unknown node type " + node);
        }
        int[] is = node.getChildren().stream().mapToInt(arg_0 -> nodes.getInt(arg_0)).toArray();
        return new CommandNodeData(suggestableNode, i, j, is);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onCommandTree(this);
    }

    public RootCommandNode<CommandSource> getCommandTree(CommandRegistryAccess commandRegistryAccess) {
        return (RootCommandNode)new CommandTree(commandRegistryAccess, this.nodes).getNode(this.rootSize);
    }

    private static /* synthetic */ boolean method_42068(BiPredicate index, List list, IntSet intSet, int i) {
        return index.test((CommandNodeData)list.get(i), intSet);
    }

    static class CommandNodeData {
        @Nullable
        final SuggestableNode suggestableNode;
        final int flags;
        final int redirectNodeIndex;
        final int[] childNodeIndices;

        CommandNodeData(@Nullable SuggestableNode suggestableNode, int flags, int redirectNodeIndex, int[] childNodeIndices) {
            this.suggestableNode = suggestableNode;
            this.flags = flags;
            this.redirectNodeIndex = redirectNodeIndex;
            this.childNodeIndices = childNodeIndices;
        }

        public void write(PacketByteBuf buf) {
            buf.writeByte(this.flags);
            buf.writeIntArray(this.childNodeIndices);
            if ((this.flags & 8) != 0) {
                buf.writeVarInt(this.redirectNodeIndex);
            }
            if (this.suggestableNode != null) {
                this.suggestableNode.write(buf);
            }
        }

        public boolean validateRedirectNodeIndex(IntSet indices) {
            if ((this.flags & 8) != 0) {
                return !indices.contains(this.redirectNodeIndex);
            }
            return true;
        }

        public boolean validateChildNodeIndices(IntSet indices) {
            for (int i : this.childNodeIndices) {
                if (!indices.contains(i)) continue;
                return false;
            }
            return true;
        }
    }

    static interface SuggestableNode {
        public ArgumentBuilder<CommandSource, ?> createArgumentBuilder(CommandRegistryAccess var1);

        public void write(PacketByteBuf var1);
    }

    static class ArgumentNode
    implements SuggestableNode {
        private final String name;
        private final ArgumentSerializer.ArgumentTypeProperties<?> properties;
        @Nullable
        private final Identifier id;

        @Nullable
        private static Identifier computeId(@Nullable SuggestionProvider<CommandSource> provider) {
            return provider != null ? SuggestionProviders.computeId(provider) : null;
        }

        ArgumentNode(String name, ArgumentSerializer.ArgumentTypeProperties<?> properties, @Nullable Identifier id) {
            this.name = name;
            this.properties = properties;
            this.id = id;
        }

        public ArgumentNode(ArgumentCommandNode<CommandSource, ?> node) {
            this(node.getName(), ArgumentTypes.getArgumentTypeProperties(node.getType()), ArgumentNode.computeId((SuggestionProvider<CommandSource>)node.getCustomSuggestions()));
        }

        @Override
        public ArgumentBuilder<CommandSource, ?> createArgumentBuilder(CommandRegistryAccess commandRegistryAccess) {
            Object argumentType = this.properties.createType(commandRegistryAccess);
            RequiredArgumentBuilder requiredArgumentBuilder = RequiredArgumentBuilder.argument((String)this.name, argumentType);
            if (this.id != null) {
                requiredArgumentBuilder.suggests(SuggestionProviders.byId(this.id));
            }
            return requiredArgumentBuilder;
        }

        @Override
        public void write(PacketByteBuf buf) {
            buf.writeString(this.name);
            ArgumentNode.write(buf, this.properties);
            if (this.id != null) {
                buf.writeIdentifier(this.id);
            }
        }

        private static <A extends ArgumentType<?>> void write(PacketByteBuf buf, ArgumentSerializer.ArgumentTypeProperties<A> properties) {
            ArgumentNode.write(buf, properties.getSerializer(), properties);
        }

        private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> void write(PacketByteBuf buf, ArgumentSerializer<A, T> serializer, ArgumentSerializer.ArgumentTypeProperties<A> properties) {
            buf.writeVarInt(Registry.COMMAND_ARGUMENT_TYPE.getRawId(serializer));
            serializer.writePacket(properties, buf);
        }
    }

    static class LiteralNode
    implements SuggestableNode {
        private final String literal;

        LiteralNode(String literal) {
            this.literal = literal;
        }

        @Override
        public ArgumentBuilder<CommandSource, ?> createArgumentBuilder(CommandRegistryAccess commandRegistryAccess) {
            return LiteralArgumentBuilder.literal((String)this.literal);
        }

        @Override
        public void write(PacketByteBuf buf) {
            buf.writeString(this.literal);
        }
    }

    static class CommandTree {
        private final CommandRegistryAccess commandRegistryAccess;
        private final List<CommandNodeData> nodeDatas;
        private final List<CommandNode<CommandSource>> nodes;

        CommandTree(CommandRegistryAccess commandRegistryAccess, List<CommandNodeData> nodeDatas) {
            this.commandRegistryAccess = commandRegistryAccess;
            this.nodeDatas = nodeDatas;
            ObjectArrayList objectArrayList = new ObjectArrayList();
            objectArrayList.size(nodeDatas.size());
            this.nodes = objectArrayList;
        }

        public CommandNode<CommandSource> getNode(int index) {
            RootCommandNode commandNode2;
            CommandNode<CommandSource> commandNode = this.nodes.get(index);
            if (commandNode != null) {
                return commandNode;
            }
            CommandNodeData commandNodeData = this.nodeDatas.get(index);
            if (commandNodeData.suggestableNode == null) {
                commandNode2 = new RootCommandNode();
            } else {
                ArgumentBuilder<CommandSource, ?> argumentBuilder = commandNodeData.suggestableNode.createArgumentBuilder(this.commandRegistryAccess);
                if ((commandNodeData.flags & 8) != 0) {
                    argumentBuilder.redirect(this.getNode(commandNodeData.redirectNodeIndex));
                }
                if ((commandNodeData.flags & 4) != 0) {
                    argumentBuilder.executes(context -> 0);
                }
                commandNode2 = argumentBuilder.build();
            }
            this.nodes.set(index, (CommandNode<CommandSource>)commandNode2);
            for (int i : commandNodeData.childNodeIndices) {
                CommandNode<CommandSource> commandNode3 = this.getNode(i);
                if (commandNode3 instanceof RootCommandNode) continue;
                commandNode2.addChild(commandNode3);
            }
            return commandNode2;
        }
    }
}

