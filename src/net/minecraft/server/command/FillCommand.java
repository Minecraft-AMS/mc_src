/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

public class FillCommand {
    private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((maxCount, count) -> Text.translatable("commands.fill.toobig", maxCount, count));
    static final BlockStateArgument AIR_BLOCK_ARGUMENT = new BlockStateArgument(Blocks.AIR.getDefaultState(), Collections.emptySet(), null);
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("commands.fill.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("fill").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("from", BlockPosArgumentType.blockPos()).then(CommandManager.argument("to", BlockPosArgumentType.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("block", BlockStateArgumentType.blockState(commandRegistryAccess)).executes(context -> FillCommand.execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)context, "block"), Mode.REPLACE, null))).then(((LiteralArgumentBuilder)CommandManager.literal("replace").executes(context -> FillCommand.execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)context, "block"), Mode.REPLACE, null))).then(CommandManager.argument("filter", BlockPredicateArgumentType.blockPredicate(commandRegistryAccess)).executes(context -> FillCommand.execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)context, "block"), Mode.REPLACE, BlockPredicateArgumentType.getBlockPredicate((CommandContext<ServerCommandSource>)context, "filter")))))).then(CommandManager.literal("keep").executes(context -> FillCommand.execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)context, "block"), Mode.REPLACE, pos -> pos.getWorld().isAir(pos.getBlockPos()))))).then(CommandManager.literal("outline").executes(context -> FillCommand.execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)context, "block"), Mode.OUTLINE, null)))).then(CommandManager.literal("hollow").executes(context -> FillCommand.execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)context, "block"), Mode.HOLLOW, null)))).then(CommandManager.literal("destroy").executes(context -> FillCommand.execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)context, "block"), Mode.DESTROY, null)))))));
    }

    private static int execute(ServerCommandSource source, BlockBox range, BlockStateArgument block, Mode mode, @Nullable Predicate<CachedBlockPosition> filter) throws CommandSyntaxException {
        int j;
        int i = range.getBlockCountX() * range.getBlockCountY() * range.getBlockCountZ();
        if (i > (j = source.getWorld().getGameRules().getInt(GameRules.COMMAND_MODIFICATION_BLOCK_LIMIT))) {
            throw TOO_BIG_EXCEPTION.create((Object)j, (Object)i);
        }
        ArrayList list = Lists.newArrayList();
        ServerWorld serverWorld = source.getWorld();
        int k = 0;
        for (BlockPos blockPos : BlockPos.iterate(range.getMinX(), range.getMinY(), range.getMinZ(), range.getMaxX(), range.getMaxY(), range.getMaxZ())) {
            BlockStateArgument blockStateArgument;
            if (filter != null && !filter.test(new CachedBlockPosition(serverWorld, blockPos, true)) || (blockStateArgument = mode.filter.filter(range, blockPos, block, serverWorld)) == null) continue;
            BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
            Clearable.clear(blockEntity);
            if (!blockStateArgument.setBlockState(serverWorld, blockPos, 2)) continue;
            list.add(blockPos.toImmutable());
            ++k;
        }
        for (BlockPos blockPos : list) {
            Block block2 = serverWorld.getBlockState(blockPos).getBlock();
            serverWorld.updateNeighbors(blockPos, block2);
        }
        if (k == 0) {
            throw FAILED_EXCEPTION.create();
        }
        source.sendFeedback(Text.translatable("commands.fill.success", k), true);
        return k;
    }

    static final class Mode
    extends Enum<Mode> {
        public static final /* enum */ Mode REPLACE = new Mode((range, pos, block, world) -> block);
        public static final /* enum */ Mode OUTLINE = new Mode((range, pos, block, world) -> {
            if (pos.getX() == range.getMinX() || pos.getX() == range.getMaxX() || pos.getY() == range.getMinY() || pos.getY() == range.getMaxY() || pos.getZ() == range.getMinZ() || pos.getZ() == range.getMaxZ()) {
                return block;
            }
            return null;
        });
        public static final /* enum */ Mode HOLLOW = new Mode((range, pos, block, world) -> {
            if (pos.getX() == range.getMinX() || pos.getX() == range.getMaxX() || pos.getY() == range.getMinY() || pos.getY() == range.getMaxY() || pos.getZ() == range.getMinZ() || pos.getZ() == range.getMaxZ()) {
                return block;
            }
            return AIR_BLOCK_ARGUMENT;
        });
        public static final /* enum */ Mode DESTROY = new Mode((range, pos, block, world) -> {
            world.breakBlock(pos, true);
            return block;
        });
        public final SetBlockCommand.Filter filter;
        private static final /* synthetic */ Mode[] field_13653;

        public static Mode[] values() {
            return (Mode[])field_13653.clone();
        }

        public static Mode valueOf(String string) {
            return Enum.valueOf(Mode.class, string);
        }

        private Mode(SetBlockCommand.Filter filter) {
            this.filter = filter;
        }

        private static /* synthetic */ Mode[] method_36968() {
            return new Mode[]{REPLACE, OUTLINE, HOLLOW, DESTROY};
        }

        static {
            field_13653 = Mode.method_36968();
        }
    }
}

