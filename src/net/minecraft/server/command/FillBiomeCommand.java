/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.function.Predicate;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.argument.RegistryEntryPredicateArgumentType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.apache.commons.lang3.mutable.MutableInt;

public class FillBiomeCommand {
    private static final int MAX_BLOCKS = 32768;
    public static final SimpleCommandExceptionType UNLOADED_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("argument.pos.unloaded"));
    private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((maximum, specified) -> Text.translatable("commands.fillbiome.toobig", maximum, specified));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("fillbiome").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("from", BlockPosArgumentType.blockPos()).then(CommandManager.argument("to", BlockPosArgumentType.blockPos()).then(((RequiredArgumentBuilder)CommandManager.argument("biome", RegistryEntryArgumentType.registryEntry(commandRegistryAccess, RegistryKeys.BIOME)).executes(context -> FillBiomeCommand.execute((ServerCommandSource)context.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "to"), RegistryEntryArgumentType.getRegistryEntry((CommandContext<ServerCommandSource>)context, "biome", RegistryKeys.BIOME), registryEntry -> true))).then(CommandManager.literal("replace").then(CommandManager.argument("filter", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.BIOME)).executes(context -> FillBiomeCommand.execute((ServerCommandSource)context.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)context, "to"), RegistryEntryArgumentType.getRegistryEntry((CommandContext<ServerCommandSource>)context, "biome", RegistryKeys.BIOME), RegistryEntryPredicateArgumentType.getRegistryEntryPredicate((CommandContext<ServerCommandSource>)context, "filter", RegistryKeys.BIOME)::test))))))));
    }

    private static int convertCoordinate(int coordinate) {
        return BiomeCoords.toBlock(BiomeCoords.fromBlock(coordinate));
    }

    private static BlockPos convertPos(BlockPos pos) {
        return new BlockPos(FillBiomeCommand.convertCoordinate(pos.getX()), FillBiomeCommand.convertCoordinate(pos.getY()), FillBiomeCommand.convertCoordinate(pos.getZ()));
    }

    private static BiomeSupplier createBiomeSupplier(MutableInt counter, Chunk chunk, BlockBox box, RegistryEntry<Biome> biome, Predicate<RegistryEntry<Biome>> filter) {
        return (x, y, z, noise) -> {
            int i = BiomeCoords.toBlock(x);
            int j = BiomeCoords.toBlock(y);
            int k = BiomeCoords.toBlock(z);
            RegistryEntry<Biome> registryEntry2 = chunk.getBiomeForNoiseGen(x, y, z);
            if (box.contains(i, j, k) && filter.test(registryEntry2)) {
                counter.increment();
                return biome;
            }
            return registryEntry2;
        };
    }

    private static int execute(ServerCommandSource source, BlockPos from, BlockPos to, RegistryEntry.Reference<Biome> biome, Predicate<RegistryEntry<Biome>> filter) throws CommandSyntaxException {
        BlockPos blockPos2;
        BlockPos blockPos = FillBiomeCommand.convertPos(from);
        BlockBox blockBox = BlockBox.create(blockPos, blockPos2 = FillBiomeCommand.convertPos(to));
        int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
        if (i > 32768) {
            throw TOO_BIG_EXCEPTION.create((Object)32768, (Object)i);
        }
        ServerWorld serverWorld = source.getWorld();
        ArrayList<Chunk> list = new ArrayList<Chunk>();
        for (int j = ChunkSectionPos.getSectionCoord(blockBox.getMinZ()); j <= ChunkSectionPos.getSectionCoord(blockBox.getMaxZ()); ++j) {
            for (int k = ChunkSectionPos.getSectionCoord(blockBox.getMinX()); k <= ChunkSectionPos.getSectionCoord(blockBox.getMaxX()); ++k) {
                Chunk chunk = serverWorld.getChunk(k, j, ChunkStatus.FULL, false);
                if (chunk == null) {
                    throw UNLOADED_EXCEPTION.create();
                }
                list.add(chunk);
            }
        }
        MutableInt mutableInt = new MutableInt(0);
        for (Chunk chunk : list) {
            chunk.populateBiomes(FillBiomeCommand.createBiomeSupplier(mutableInt, chunk, blockBox, biome, filter), serverWorld.getChunkManager().getNoiseConfig().getMultiNoiseSampler());
            chunk.setNeedsSaving(true);
            serverWorld.getChunkManager().threadedAnvilChunkStorage.sendChunkPacketToWatchingPlayers(chunk);
        }
        source.sendFeedback(Text.translatable("commands.fillbiome.success.count", mutableInt.getValue(), blockBox.getMinX(), blockBox.getMinY(), blockBox.getMinZ(), blockBox.getMaxX(), blockBox.getMaxY(), blockBox.getMaxZ()), true);
        return mutableInt.getValue();
    }
}

