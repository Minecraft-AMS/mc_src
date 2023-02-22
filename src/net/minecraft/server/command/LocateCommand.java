/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.google.common.base.Ticker
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.server.command;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.Optional;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryEntryPredicateArgumentType;
import net.minecraft.command.argument.RegistryPredicateArgumentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.slf4j.Logger;

public class LocateCommand {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final DynamicCommandExceptionType STRUCTURE_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("commands.locate.structure.not_found", id));
    private static final DynamicCommandExceptionType STRUCTURE_INVALID_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("commands.locate.structure.invalid", id));
    private static final DynamicCommandExceptionType BIOME_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("commands.locate.biome.not_found", id));
    private static final DynamicCommandExceptionType POI_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("commands.locate.poi.not_found", id));
    private static final int LOCATE_STRUCTURE_RADIUS = 100;
    private static final int LOCATE_BIOME_RADIUS = 6400;
    private static final int LOCATE_BIOME_HORIZONTAL_BLOCK_CHECK_INTERVAL = 32;
    private static final int LOCATE_BIOME_VERTICAL_BLOCK_CHECK_INTERVAL = 64;
    private static final int LOCATE_POI_RADIUS = 256;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("locate").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.literal("structure").then(CommandManager.argument("structure", RegistryPredicateArgumentType.registryPredicate(RegistryKeys.STRUCTURE)).executes(context -> LocateCommand.executeLocateStructure((ServerCommandSource)context.getSource(), RegistryPredicateArgumentType.getPredicate((CommandContext<ServerCommandSource>)context, "structure", RegistryKeys.STRUCTURE, STRUCTURE_INVALID_EXCEPTION)))))).then(CommandManager.literal("biome").then(CommandManager.argument("biome", RegistryEntryPredicateArgumentType.registryEntryPredicate(registryAccess, RegistryKeys.BIOME)).executes(context -> LocateCommand.executeLocateBiome((ServerCommandSource)context.getSource(), RegistryEntryPredicateArgumentType.getRegistryEntryPredicate((CommandContext<ServerCommandSource>)context, "biome", RegistryKeys.BIOME)))))).then(CommandManager.literal("poi").then(CommandManager.argument("poi", RegistryEntryPredicateArgumentType.registryEntryPredicate(registryAccess, RegistryKeys.POINT_OF_INTEREST_TYPE)).executes(context -> LocateCommand.executeLocatePoi((ServerCommandSource)context.getSource(), RegistryEntryPredicateArgumentType.getRegistryEntryPredicate((CommandContext<ServerCommandSource>)context, "poi", RegistryKeys.POINT_OF_INTEREST_TYPE))))));
    }

    private static Optional<? extends RegistryEntryList.ListBacked<Structure>> getStructureListForPredicate(RegistryPredicateArgumentType.RegistryPredicate<Structure> predicate, Registry<Structure> structureRegistry) {
        return (Optional)predicate.getKey().map(key -> structureRegistry.getEntry((RegistryKey<Structure>)key).map(entry -> RegistryEntryList.of(entry)), structureRegistry::getEntryList);
    }

    private static int executeLocateStructure(ServerCommandSource source, RegistryPredicateArgumentType.RegistryPredicate<Structure> predicate) throws CommandSyntaxException {
        Registry<Structure> registry = source.getWorld().getRegistryManager().get(RegistryKeys.STRUCTURE);
        RegistryEntryList registryEntryList = LocateCommand.getStructureListForPredicate(predicate, registry).orElseThrow(() -> STRUCTURE_INVALID_EXCEPTION.create((Object)predicate.asString()));
        BlockPos blockPos = new BlockPos(source.getPosition());
        ServerWorld serverWorld = source.getWorld();
        Stopwatch stopwatch = Stopwatch.createStarted((Ticker)Util.TICKER);
        Pair<BlockPos, RegistryEntry<Structure>> pair = serverWorld.getChunkManager().getChunkGenerator().locateStructure(serverWorld, registryEntryList, blockPos, 100, false);
        stopwatch.stop();
        if (pair == null) {
            throw STRUCTURE_NOT_FOUND_EXCEPTION.create((Object)predicate.asString());
        }
        return LocateCommand.sendCoordinates(source, predicate, blockPos, pair, "commands.locate.structure.success", false, stopwatch.elapsed());
    }

    private static int executeLocateBiome(ServerCommandSource source, RegistryEntryPredicateArgumentType.EntryPredicate<Biome> predicate) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(source.getPosition());
        Stopwatch stopwatch = Stopwatch.createStarted((Ticker)Util.TICKER);
        Pair<BlockPos, RegistryEntry<Biome>> pair = source.getWorld().locateBiome(predicate, blockPos, 6400, 32, 64);
        stopwatch.stop();
        if (pair == null) {
            throw BIOME_NOT_FOUND_EXCEPTION.create((Object)predicate.asString());
        }
        return LocateCommand.sendCoordinates(source, predicate, blockPos, pair, "commands.locate.biome.success", true, stopwatch.elapsed());
    }

    private static int executeLocatePoi(ServerCommandSource source, RegistryEntryPredicateArgumentType.EntryPredicate<PointOfInterestType> predicate) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(source.getPosition());
        ServerWorld serverWorld = source.getWorld();
        Stopwatch stopwatch = Stopwatch.createStarted((Ticker)Util.TICKER);
        Optional<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> optional = serverWorld.getPointOfInterestStorage().getNearestTypeAndPosition(predicate, blockPos, 256, PointOfInterestStorage.OccupationStatus.ANY);
        stopwatch.stop();
        if (optional.isEmpty()) {
            throw POI_NOT_FOUND_EXCEPTION.create((Object)predicate.asString());
        }
        return LocateCommand.sendCoordinates(source, predicate, blockPos, optional.get().swap(), "commands.locate.poi.success", false, stopwatch.elapsed());
    }

    private static String getKeyString(Pair<BlockPos, ? extends RegistryEntry<?>> result) {
        return ((RegistryEntry)result.getSecond()).getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
    }

    public static int sendCoordinates(ServerCommandSource source, RegistryEntryPredicateArgumentType.EntryPredicate<?> predicate, BlockPos currentPos, Pair<BlockPos, ? extends RegistryEntry<?>> result, String successMessage, boolean includeY, Duration timeTaken) {
        String string = (String)predicate.getEntry().map(entry -> predicate.asString(), tag -> predicate.asString() + " (" + LocateCommand.getKeyString(result) + ")");
        return LocateCommand.sendCoordinates(source, currentPos, result, successMessage, includeY, string, timeTaken);
    }

    public static int sendCoordinates(ServerCommandSource source, RegistryPredicateArgumentType.RegistryPredicate<?> structure, BlockPos currentPos, Pair<BlockPos, ? extends RegistryEntry<?>> result, String successMessage, boolean includeY, Duration timeTaken) {
        String string = (String)structure.getKey().map(key -> key.getValue().toString(), key -> "#" + key.id() + " (" + LocateCommand.getKeyString(result) + ")");
        return LocateCommand.sendCoordinates(source, currentPos, result, successMessage, includeY, string, timeTaken);
    }

    private static int sendCoordinates(ServerCommandSource source, BlockPos currentPos, Pair<BlockPos, ? extends RegistryEntry<?>> result, String successMessage, boolean includeY, String entryString, Duration timeTaken) {
        BlockPos blockPos = (BlockPos)result.getFirst();
        int i = includeY ? MathHelper.floor(MathHelper.sqrt((float)currentPos.getSquaredDistance(blockPos))) : MathHelper.floor(LocateCommand.getDistance(currentPos.getX(), currentPos.getZ(), blockPos.getX(), blockPos.getZ()));
        String string = includeY ? String.valueOf(blockPos.getY()) : "~";
        MutableText text = Texts.bracketed(Text.translatable("chat.coordinates", blockPos.getX(), string, blockPos.getZ())).styled(style -> style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + blockPos.getX() + " " + string + " " + blockPos.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.coordinates.tooltip"))));
        source.sendFeedback(Text.translatable(successMessage, entryString, text, i), false);
        LOGGER.info("Locating element " + entryString + " took " + timeTaken.toMillis() + " ms");
        return i;
    }

    private static float getDistance(int x1, int y1, int x2, int y2) {
        int i = x2 - x1;
        int j = y2 - y1;
        return MathHelper.sqrt(i * i + j * j);
    }
}

