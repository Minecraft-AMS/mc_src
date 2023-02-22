/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.Tag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NbtPathArgumentType
implements ArgumentType<NbtPath> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
    public static final SimpleCommandExceptionType INVALID_PATH_NODE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("arguments.nbtpath.node.invalid", new Object[0]));
    public static final DynamicCommandExceptionType NOTHING_FOUND_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.nbtpath.nothing_found", object));

    public static NbtPathArgumentType nbtPath() {
        return new NbtPathArgumentType();
    }

    public static NbtPath getNbtPath(CommandContext<ServerCommandSource> context, String name) {
        return (NbtPath)context.getArgument(name, NbtPath.class);
    }

    public NbtPath parse(StringReader stringReader) throws CommandSyntaxException {
        ArrayList list = Lists.newArrayList();
        int i = stringReader.getCursor();
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        boolean bl = true;
        while (stringReader.canRead() && stringReader.peek() != ' ') {
            char c;
            NbtPathNode nbtPathNode = NbtPathArgumentType.parseNode(stringReader, bl);
            list.add(nbtPathNode);
            object2IntMap.put((Object)nbtPathNode, stringReader.getCursor() - i);
            bl = false;
            if (!stringReader.canRead() || (c = stringReader.peek()) == ' ' || c == '[' || c == '{') continue;
            stringReader.expect('.');
        }
        return new NbtPath(stringReader.getString().substring(i, stringReader.getCursor()), list.toArray(new NbtPathNode[0]), (Object2IntMap<NbtPathNode>)object2IntMap);
    }

    private static NbtPathNode parseNode(StringReader reader, boolean root) throws CommandSyntaxException {
        switch (reader.peek()) {
            case '{': {
                if (!root) {
                    throw INVALID_PATH_NODE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
                }
                CompoundTag compoundTag = new StringNbtReader(reader).parseCompoundTag();
                return new EqualCompoundNode(compoundTag);
            }
            case '[': {
                reader.skip();
                char i = reader.peek();
                if (i == '{') {
                    CompoundTag compoundTag2 = new StringNbtReader(reader).parseCompoundTag();
                    reader.expect(']');
                    return new EqualListElementNode(compoundTag2);
                }
                if (i == ']') {
                    reader.skip();
                    return AllListElementsNode.INSTANCE;
                }
                int j = reader.readInt();
                reader.expect(']');
                return new ListIndexNode(j);
            }
            case '\"': {
                String string = reader.readString();
                return NbtPathArgumentType.readCompoundChildNode(reader, string);
            }
        }
        String string = NbtPathArgumentType.readName(reader);
        return NbtPathArgumentType.readCompoundChildNode(reader, string);
    }

    private static NbtPathNode readCompoundChildNode(StringReader reader, String name) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '{') {
            CompoundTag compoundTag = new StringNbtReader(reader).parseCompoundTag();
            return new EqualCompundChildNode(name, compoundTag);
        }
        return new CompoundChildNode(name);
    }

    private static String readName(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        while (reader.canRead() && NbtPathArgumentType.isNameCharacter(reader.peek())) {
            reader.skip();
        }
        if (reader.getCursor() == i) {
            throw INVALID_PATH_NODE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        return reader.getString().substring(i, reader.getCursor());
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static boolean isNameCharacter(char c) {
        return c != ' ' && c != '\"' && c != '[' && c != ']' && c != '.' && c != '{' && c != '}';
    }

    private static Predicate<Tag> getPredicate(CompoundTag filter) {
        return tag -> NbtHelper.matches(filter, tag, true);
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    static class EqualCompoundNode
    implements NbtPathNode {
        private final Predicate<Tag> predicate;

        public EqualCompoundNode(CompoundTag tag) {
            this.predicate = NbtPathArgumentType.getPredicate(tag);
        }

        @Override
        public void get(Tag tag, List<Tag> results) {
            if (tag instanceof CompoundTag && this.predicate.test(tag)) {
                results.add(tag);
            }
        }

        @Override
        public void putIfAbsent(Tag tag, Supplier<Tag> supplier, List<Tag> results) {
            this.get(tag, results);
        }

        @Override
        public Tag createParent() {
            return new CompoundTag();
        }

        @Override
        public int put(Tag tag, Supplier<Tag> supplier) {
            return 0;
        }

        @Override
        public int remove(Tag tag) {
            return 0;
        }
    }

    static class EqualCompundChildNode
    implements NbtPathNode {
        private final String name;
        private final CompoundTag tag;
        private final Predicate<Tag> predicate;

        public EqualCompundChildNode(String name, CompoundTag tag) {
            this.name = name;
            this.tag = tag;
            this.predicate = NbtPathArgumentType.getPredicate(tag);
        }

        @Override
        public void get(Tag tag, List<Tag> results) {
            Tag tag2;
            if (tag instanceof CompoundTag && this.predicate.test(tag2 = ((CompoundTag)tag).get(this.name))) {
                results.add(tag2);
            }
        }

        @Override
        public void putIfAbsent(Tag tag, Supplier<Tag> supplier, List<Tag> results) {
            if (tag instanceof CompoundTag) {
                CompoundTag compoundTag = (CompoundTag)tag;
                Tag tag2 = compoundTag.get(this.name);
                if (tag2 == null) {
                    tag2 = this.tag.copy();
                    compoundTag.put(this.name, tag2);
                    results.add(tag2);
                } else if (this.predicate.test(tag2)) {
                    results.add(tag2);
                }
            }
        }

        @Override
        public Tag createParent() {
            return new CompoundTag();
        }

        @Override
        public int put(Tag tag, Supplier<Tag> supplier) {
            Tag tag3;
            CompoundTag compoundTag;
            Tag tag2;
            if (tag instanceof CompoundTag && this.predicate.test(tag2 = (compoundTag = (CompoundTag)tag).get(this.name)) && !(tag3 = supplier.get()).equals(tag2)) {
                compoundTag.put(this.name, tag3);
                return 1;
            }
            return 0;
        }

        @Override
        public int remove(Tag tag) {
            CompoundTag compoundTag;
            Tag tag2;
            if (tag instanceof CompoundTag && this.predicate.test(tag2 = (compoundTag = (CompoundTag)tag).get(this.name))) {
                compoundTag.remove(this.name);
                return 1;
            }
            return 0;
        }
    }

    static class AllListElementsNode
    implements NbtPathNode {
        public static final AllListElementsNode INSTANCE = new AllListElementsNode();

        private AllListElementsNode() {
        }

        @Override
        public void get(Tag tag, List<Tag> results) {
            if (tag instanceof AbstractListTag) {
                results.addAll((AbstractListTag)tag);
            }
        }

        @Override
        public void putIfAbsent(Tag tag, Supplier<Tag> supplier, List<Tag> results) {
            if (tag instanceof AbstractListTag) {
                AbstractListTag abstractListTag = (AbstractListTag)tag;
                if (abstractListTag.isEmpty()) {
                    Tag tag2 = supplier.get();
                    if (abstractListTag.addTag(0, tag2)) {
                        results.add(tag2);
                    }
                } else {
                    results.addAll(abstractListTag);
                }
            }
        }

        @Override
        public Tag createParent() {
            return new ListTag();
        }

        @Override
        public int put(Tag tag, Supplier<Tag> supplier) {
            if (tag instanceof AbstractListTag) {
                AbstractListTag abstractListTag = (AbstractListTag)tag;
                int i = abstractListTag.size();
                if (i == 0) {
                    abstractListTag.addTag(0, supplier.get());
                    return 1;
                }
                Tag tag2 = supplier.get();
                int j = i - (int)abstractListTag.stream().filter(tag2::equals).count();
                if (j == 0) {
                    return 0;
                }
                abstractListTag.clear();
                if (!abstractListTag.addTag(0, tag2)) {
                    return 0;
                }
                for (int k = 1; k < i; ++k) {
                    abstractListTag.addTag(k, supplier.get());
                }
                return j;
            }
            return 0;
        }

        @Override
        public int remove(Tag tag) {
            AbstractListTag abstractListTag;
            int i;
            if (tag instanceof AbstractListTag && (i = (abstractListTag = (AbstractListTag)tag).size()) > 0) {
                abstractListTag.clear();
                return i;
            }
            return 0;
        }
    }

    static class EqualListElementNode
    implements NbtPathNode {
        private final CompoundTag tag;
        private final Predicate<Tag> predicate;

        public EqualListElementNode(CompoundTag compoundTag) {
            this.tag = compoundTag;
            this.predicate = NbtPathArgumentType.getPredicate(compoundTag);
        }

        @Override
        public void get(Tag tag, List<Tag> results) {
            if (tag instanceof ListTag) {
                ListTag listTag = (ListTag)tag;
                listTag.stream().filter(this.predicate).forEach(results::add);
            }
        }

        @Override
        public void putIfAbsent(Tag tag2, Supplier<Tag> supplier, List<Tag> results) {
            MutableBoolean mutableBoolean = new MutableBoolean();
            if (tag2 instanceof ListTag) {
                ListTag listTag = (ListTag)tag2;
                listTag.stream().filter(this.predicate).forEach(tag -> {
                    results.add((Tag)tag);
                    mutableBoolean.setTrue();
                });
                if (mutableBoolean.isFalse()) {
                    CompoundTag compoundTag = this.tag.copy();
                    listTag.add(compoundTag);
                    results.add(compoundTag);
                }
            }
        }

        @Override
        public Tag createParent() {
            return new ListTag();
        }

        @Override
        public int put(Tag tag, Supplier<Tag> supplier) {
            int i = 0;
            if (tag instanceof ListTag) {
                ListTag listTag = (ListTag)tag;
                int j = listTag.size();
                if (j == 0) {
                    listTag.add(supplier.get());
                    ++i;
                } else {
                    for (int k = 0; k < j; ++k) {
                        Tag tag3;
                        Tag tag2 = listTag.get(k);
                        if (!this.predicate.test(tag2) || (tag3 = supplier.get()).equals(tag2) || !listTag.setTag(k, tag3)) continue;
                        ++i;
                    }
                }
            }
            return i;
        }

        @Override
        public int remove(Tag tag) {
            int i = 0;
            if (tag instanceof ListTag) {
                ListTag listTag = (ListTag)tag;
                for (int j = listTag.size() - 1; j >= 0; --j) {
                    if (!this.predicate.test(listTag.get(j))) continue;
                    listTag.remove(j);
                    ++i;
                }
            }
            return i;
        }
    }

    static class ListIndexNode
    implements NbtPathNode {
        private final int index;

        public ListIndexNode(int index) {
            this.index = index;
        }

        @Override
        public void get(Tag tag, List<Tag> results) {
            if (tag instanceof AbstractListTag) {
                int j;
                AbstractListTag abstractListTag = (AbstractListTag)tag;
                int i = abstractListTag.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    results.add((Tag)abstractListTag.get(j));
                }
            }
        }

        @Override
        public void putIfAbsent(Tag tag, Supplier<Tag> supplier, List<Tag> results) {
            this.get(tag, results);
        }

        @Override
        public Tag createParent() {
            return new ListTag();
        }

        @Override
        public int put(Tag tag, Supplier<Tag> supplier) {
            if (tag instanceof AbstractListTag) {
                int j;
                AbstractListTag abstractListTag = (AbstractListTag)tag;
                int i = abstractListTag.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    Tag tag2 = (Tag)abstractListTag.get(j);
                    Tag tag3 = supplier.get();
                    if (!tag3.equals(tag2) && abstractListTag.setTag(j, tag3)) {
                        return 1;
                    }
                }
            }
            return 0;
        }

        @Override
        public int remove(Tag tag) {
            if (tag instanceof AbstractListTag) {
                int j;
                AbstractListTag abstractListTag = (AbstractListTag)tag;
                int i = abstractListTag.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    abstractListTag.remove(j);
                    return 1;
                }
            }
            return 0;
        }
    }

    static class CompoundChildNode
    implements NbtPathNode {
        private final String name;

        public CompoundChildNode(String string) {
            this.name = string;
        }

        @Override
        public void get(Tag tag, List<Tag> results) {
            Tag tag2;
            if (tag instanceof CompoundTag && (tag2 = ((CompoundTag)tag).get(this.name)) != null) {
                results.add(tag2);
            }
        }

        @Override
        public void putIfAbsent(Tag tag, Supplier<Tag> supplier, List<Tag> results) {
            if (tag instanceof CompoundTag) {
                Tag tag2;
                CompoundTag compoundTag = (CompoundTag)tag;
                if (compoundTag.contains(this.name)) {
                    tag2 = compoundTag.get(this.name);
                } else {
                    tag2 = supplier.get();
                    compoundTag.put(this.name, tag2);
                }
                results.add(tag2);
            }
        }

        @Override
        public Tag createParent() {
            return new CompoundTag();
        }

        @Override
        public int put(Tag tag, Supplier<Tag> supplier) {
            if (tag instanceof CompoundTag) {
                Tag tag3;
                CompoundTag compoundTag = (CompoundTag)tag;
                Tag tag2 = supplier.get();
                if (!tag2.equals(tag3 = compoundTag.put(this.name, tag2))) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public int remove(Tag tag) {
            CompoundTag compoundTag;
            if (tag instanceof CompoundTag && (compoundTag = (CompoundTag)tag).contains(this.name)) {
                compoundTag.remove(this.name);
                return 1;
            }
            return 0;
        }
    }

    static interface NbtPathNode {
        public void get(Tag var1, List<Tag> var2);

        public void putIfAbsent(Tag var1, Supplier<Tag> var2, List<Tag> var3);

        public Tag createParent();

        public int put(Tag var1, Supplier<Tag> var2);

        public int remove(Tag var1);

        default public List<Tag> get(List<Tag> tags) {
            return this.get(tags, this::get);
        }

        default public List<Tag> putIfAbsent(List<Tag> tags, Supplier<Tag> supplier) {
            return this.get(tags, (Tag tag, List<Tag> list) -> this.putIfAbsent((Tag)tag, supplier, (List<Tag>)list));
        }

        default public List<Tag> get(List<Tag> tags, BiConsumer<Tag, List<Tag>> getter) {
            ArrayList list = Lists.newArrayList();
            for (Tag tag : tags) {
                getter.accept(tag, list);
            }
            return list;
        }
    }

    public static class NbtPath {
        private final String string;
        private final Object2IntMap<NbtPathNode> nodeEndIndices;
        private final NbtPathNode[] nodes;

        public NbtPath(String string, NbtPathNode[] nodes, Object2IntMap<NbtPathNode> nodeEndIndices) {
            this.string = string;
            this.nodes = nodes;
            this.nodeEndIndices = nodeEndIndices;
        }

        public List<Tag> get(Tag tag) throws CommandSyntaxException {
            List<Tag> list = Collections.singletonList(tag);
            for (NbtPathNode nbtPathNode : this.nodes) {
                if (!(list = nbtPathNode.get(list)).isEmpty()) continue;
                throw this.createNothingFoundException(nbtPathNode);
            }
            return list;
        }

        public int count(Tag tag) {
            List<Tag> list = Collections.singletonList(tag);
            for (NbtPathNode nbtPathNode : this.nodes) {
                if (!(list = nbtPathNode.get(list)).isEmpty()) continue;
                return 0;
            }
            return list.size();
        }

        private List<Tag> getParents(Tag tag) throws CommandSyntaxException {
            List<Tag> list = Collections.singletonList(tag);
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                NbtPathNode nbtPathNode = this.nodes[i];
                int j = i + 1;
                if (!(list = nbtPathNode.putIfAbsent(list, this.nodes[j]::createParent)).isEmpty()) continue;
                throw this.createNothingFoundException(nbtPathNode);
            }
            return list;
        }

        public List<Tag> putIfAbsent(Tag tag, Supplier<Tag> supplier) throws CommandSyntaxException {
            List<Tag> list = this.getParents(tag);
            NbtPathNode nbtPathNode = this.nodes[this.nodes.length - 1];
            return nbtPathNode.putIfAbsent(list, supplier);
        }

        private static int forEach(List<Tag> tags, Function<Tag, Integer> function) {
            return tags.stream().map(function).reduce(0, (integer, integer2) -> integer + integer2);
        }

        public int put(Tag tag2, Supplier<Tag> supplier) throws CommandSyntaxException {
            List<Tag> list = this.getParents(tag2);
            NbtPathNode nbtPathNode = this.nodes[this.nodes.length - 1];
            return NbtPath.forEach(list, tag -> nbtPathNode.put((Tag)tag, supplier));
        }

        public int remove(Tag tag) {
            List<Tag> list = Collections.singletonList(tag);
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                list = this.nodes[i].get(list);
            }
            NbtPathNode nbtPathNode = this.nodes[this.nodes.length - 1];
            return NbtPath.forEach(list, nbtPathNode::remove);
        }

        private CommandSyntaxException createNothingFoundException(NbtPathNode node) {
            int i = this.nodeEndIndices.getInt((Object)node);
            return NOTHING_FOUND_EXCEPTION.create((Object)this.string.substring(0, i));
        }

        public String toString() {
            return this.string;
        }
    }
}

