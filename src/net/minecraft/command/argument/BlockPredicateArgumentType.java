/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class BlockPredicateArgumentType
implements ArgumentType<BlockPredicate> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
    private static final DynamicCommandExceptionType UNKNOWN_TAG_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.block.tag.unknown", object));

    public static BlockPredicateArgumentType blockPredicate() {
        return new BlockPredicateArgumentType();
    }

    public BlockPredicate parse(StringReader stringReader) throws CommandSyntaxException {
        BlockArgumentParser blockArgumentParser = new BlockArgumentParser(stringReader, true).parse(true);
        if (blockArgumentParser.getBlockState() != null) {
            StatePredicate statePredicate = new StatePredicate(blockArgumentParser.getBlockState(), blockArgumentParser.getBlockProperties().keySet(), blockArgumentParser.getNbtData());
            return tagManager -> statePredicate;
        }
        Identifier identifier = blockArgumentParser.getTagId();
        return tagManager -> {
            Tag<Block> tag = tagManager.getBlocks().getTag(identifier);
            if (tag == null) {
                throw UNKNOWN_TAG_EXCEPTION.create((Object)identifier.toString());
            }
            return new TagPredicate(tag, blockArgumentParser.getProperties(), blockArgumentParser.getNbtData());
        };
    }

    public static Predicate<CachedBlockPosition> getBlockPredicate(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ((BlockPredicate)context.getArgument(name, BlockPredicate.class)).create(((ServerCommandSource)context.getSource()).getMinecraftServer().getTagManager());
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());
        BlockArgumentParser blockArgumentParser = new BlockArgumentParser(stringReader, true);
        try {
            blockArgumentParser.parse(true);
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return blockArgumentParser.getSuggestions(builder, BlockTags.getTagGroup());
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    static class TagPredicate
    implements Predicate<CachedBlockPosition> {
        private final Tag<Block> tag;
        @Nullable
        private final NbtCompound nbt;
        private final Map<String, String> properties;

        private TagPredicate(Tag<Block> tag, Map<String, String> map, @Nullable NbtCompound nbt) {
            this.tag = tag;
            this.properties = map;
            this.nbt = nbt;
        }

        @Override
        public boolean test(CachedBlockPosition cachedBlockPosition) {
            BlockState blockState = cachedBlockPosition.getBlockState();
            if (!blockState.isIn(this.tag)) {
                return false;
            }
            for (Map.Entry<String, String> entry : this.properties.entrySet()) {
                Property<?> property = blockState.getBlock().getStateManager().getProperty(entry.getKey());
                if (property == null) {
                    return false;
                }
                Comparable comparable = property.parse(entry.getValue()).orElse(null);
                if (comparable == null) {
                    return false;
                }
                if (blockState.get(property) == comparable) continue;
                return false;
            }
            if (this.nbt != null) {
                BlockEntity blockEntity = cachedBlockPosition.getBlockEntity();
                return blockEntity != null && NbtHelper.matches(this.nbt, blockEntity.writeNbt(new NbtCompound()), true);
            }
            return true;
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((CachedBlockPosition)object);
        }
    }

    static class StatePredicate
    implements Predicate<CachedBlockPosition> {
        private final BlockState state;
        private final Set<Property<?>> properties;
        @Nullable
        private final NbtCompound nbt;

        public StatePredicate(BlockState state, Set<Property<?>> properties, @Nullable NbtCompound nbt) {
            this.state = state;
            this.properties = properties;
            this.nbt = nbt;
        }

        @Override
        public boolean test(CachedBlockPosition cachedBlockPosition) {
            BlockState blockState = cachedBlockPosition.getBlockState();
            if (!blockState.isOf(this.state.getBlock())) {
                return false;
            }
            for (Property<?> property : this.properties) {
                if (blockState.get(property) == this.state.get(property)) continue;
                return false;
            }
            if (this.nbt != null) {
                BlockEntity blockEntity = cachedBlockPosition.getBlockEntity();
                return blockEntity != null && NbtHelper.matches(this.nbt, blockEntity.writeNbt(new NbtCompound()), true);
            }
            return true;
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((CachedBlockPosition)object);
        }
    }

    public static interface BlockPredicate {
        public Predicate<CachedBlockPosition> create(TagManager var1) throws CommandSyntaxException;
    }
}

