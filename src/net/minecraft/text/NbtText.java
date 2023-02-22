/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.text;

import com.google.common.base.Joiner;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.ParsableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class NbtText
extends BaseText
implements ParsableText {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final boolean interpret;
    protected final String rawPath;
    @Nullable
    protected final NbtPathArgumentType.NbtPath path;

    @Nullable
    private static NbtPathArgumentType.NbtPath parsePath(String rawPath) {
        try {
            return new NbtPathArgumentType().parse(new StringReader(rawPath));
        }
        catch (CommandSyntaxException commandSyntaxException) {
            return null;
        }
    }

    public NbtText(String rawPath, boolean interpret) {
        this(rawPath, NbtText.parsePath(rawPath), interpret);
    }

    protected NbtText(String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret) {
        this.rawPath = rawPath;
        this.path = path;
        this.interpret = interpret;
    }

    protected abstract Stream<CompoundTag> toNbt(ServerCommandSource var1) throws CommandSyntaxException;

    @Override
    public String asString() {
        return "";
    }

    public String getPath() {
        return this.rawPath;
    }

    public boolean shouldInterpret() {
        return this.interpret;
    }

    @Override
    public Text parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        if (source == null || this.path == null) {
            return new LiteralText("");
        }
        Stream<String> stream = this.toNbt(source).flatMap(nbt -> {
            try {
                return this.path.get((Tag)nbt).stream();
            }
            catch (CommandSyntaxException commandSyntaxException) {
                return Stream.empty();
            }
        }).map(Tag::asString);
        if (this.interpret) {
            return stream.flatMap(text -> {
                try {
                    Text text2 = Text.Serializer.fromJson(text);
                    return Stream.of(Texts.parse(source, text2, sender, depth));
                }
                catch (Exception exception) {
                    LOGGER.warn("Failed to parse component: " + text, (Throwable)exception);
                    return Stream.of(new Text[0]);
                }
            }).reduce((a, b) -> a.append(", ").append((Text)b)).orElse(new LiteralText(""));
        }
        return new LiteralText(Joiner.on((String)", ").join(stream.iterator()));
    }

    public static class BlockNbtText
    extends NbtText {
        private final String rawPos;
        @Nullable
        private final PosArgument pos;

        public BlockNbtText(String rawPath, boolean rawJson, String rawPos) {
            super(rawPath, rawJson);
            this.rawPos = rawPos;
            this.pos = this.parsePos(this.rawPos);
        }

        @Nullable
        private PosArgument parsePos(String rawPos) {
            try {
                return BlockPosArgumentType.blockPos().parse(new StringReader(rawPos));
            }
            catch (CommandSyntaxException commandSyntaxException) {
                return null;
            }
        }

        private BlockNbtText(String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret, String rawPos, @Nullable PosArgument pos) {
            super(rawPath, path, interpret);
            this.rawPos = rawPos;
            this.pos = pos;
        }

        @Nullable
        public String getPos() {
            return this.rawPos;
        }

        @Override
        public Text copy() {
            return new BlockNbtText(this.rawPath, this.path, this.interpret, this.rawPos, this.pos);
        }

        @Override
        protected Stream<CompoundTag> toNbt(ServerCommandSource source) {
            BlockEntity blockEntity;
            BlockPos blockPos;
            ServerWorld serverWorld;
            if (this.pos != null && (serverWorld = source.getWorld()).canSetBlock(blockPos = this.pos.toAbsoluteBlockPos(source)) && (blockEntity = serverWorld.getBlockEntity(blockPos)) != null) {
                return Stream.of(blockEntity.toTag(new CompoundTag()));
            }
            return Stream.empty();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof BlockNbtText) {
                BlockNbtText blockNbtText = (BlockNbtText)o;
                return Objects.equals(this.rawPos, blockNbtText.rawPos) && Objects.equals(this.rawPath, blockNbtText.rawPath) && super.equals(o);
            }
            return false;
        }

        @Override
        public String toString() {
            return "BlockPosArgument{pos='" + this.rawPos + '\'' + "path='" + this.rawPath + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
        }
    }

    public static class EntityNbtText
    extends NbtText {
        private final String rawSelector;
        @Nullable
        private final EntitySelector selector;

        public EntityNbtText(String rawPath, boolean interpret, String rawSelector) {
            super(rawPath, interpret);
            this.rawSelector = rawSelector;
            this.selector = EntityNbtText.parseSelector(rawSelector);
        }

        @Nullable
        private static EntitySelector parseSelector(String rawSelector) {
            try {
                EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(rawSelector));
                return entitySelectorReader.read();
            }
            catch (CommandSyntaxException commandSyntaxException) {
                return null;
            }
        }

        private EntityNbtText(String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret, String rawSelector, @Nullable EntitySelector selector) {
            super(rawPath, path, interpret);
            this.rawSelector = rawSelector;
            this.selector = selector;
        }

        public String getSelector() {
            return this.rawSelector;
        }

        @Override
        public Text copy() {
            return new EntityNbtText(this.rawPath, this.path, this.interpret, this.rawSelector, this.selector);
        }

        @Override
        protected Stream<CompoundTag> toNbt(ServerCommandSource source) throws CommandSyntaxException {
            if (this.selector != null) {
                List<? extends Entity> list = this.selector.getEntities(source);
                return list.stream().map(NbtPredicate::entityToTag);
            }
            return Stream.empty();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof EntityNbtText) {
                EntityNbtText entityNbtText = (EntityNbtText)o;
                return Objects.equals(this.rawSelector, entityNbtText.rawSelector) && Objects.equals(this.rawPath, entityNbtText.rawPath) && super.equals(o);
            }
            return false;
        }

        @Override
        public String toString() {
            return "EntityNbtComponent{selector='" + this.rawSelector + '\'' + "path='" + this.rawPath + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
        }
    }
}

